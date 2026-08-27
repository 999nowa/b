package net.osmand.plus.addressocr;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.plus.OsmandApplication;
import net.osmand.plus.googlemaps.GoogleAddressSearchClient;
import net.osmand.plus.helpers.TargetPoint;
import net.osmand.plus.helpers.TargetPointsHelper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/** Camera -> local OCR -> address geocoding -> append addresses to the current route. */
public class AddressOcrRouteActivity extends Activity {
    private static final int REQUEST_CAMERA = 7001;
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
            "(?i)\\b(?:[a-zåäöéü0-9][a-zåäöéü0-9 .’'/-]{1,60})\\s+\\d{1,4}(?:\\s*[a-zåäö]?)?(?:\\s*[-/]\\s*\\d{1,4})?\\b");

    private Uri photoUri;
    private TextView status;
    private Button scanButton;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);

        TextView title = new TextView(this);
        title.setText("Skanna adresser till rutt");
        title.setTextSize(22);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        status = new TextView(this);
        status.setText("Fotografera en adresslista eller etiketter.");
        status.setPadding(0, 24, 0, 24);
        root.addView(status, new LinearLayout.LayoutParams(-1, -2));

        scanButton = new Button(this);
        scanButton.setText("Fotografera adresser");
        scanButton.setOnClickListener(v -> openCamera());
        root.addView(scanButton, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "osmand_address_scan_" + System.currentTimeMillis() + ".jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (photoUri == null) {
            Toast.makeText(this, "Kunde inte skapa kamerabild.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else if (requestCode == REQUEST_CAMERA) {
            Toast.makeText(this, "Kamerabehörighet krävs för att skanna adresser.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CAMERA) return;
        if (resultCode != RESULT_OK || photoUri == null) {
            status.setText("Fotograferingen avbröts.");
            return;
        }
        scanButton.setEnabled(false);
        status.setText("Läser text lokalt med OCR…");
        try {
            InputImage image = InputImage.fromFilePath(this, photoUri);
            TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
            recognizer.process(image)
                    .addOnSuccessListener(this::processOcrText)
                    .addOnFailureListener(e -> {
                        scanButton.setEnabled(true);
                        status.setText("OCR misslyckades: " + e.getMessage());
                    })
                    .addOnCompleteListener(task -> recognizer.close());
        } catch (Exception e) {
            scanButton.setEnabled(true);
            status.setText("Kunde inte läsa bilden: " + e.getMessage());
        }
    }

    private void processOcrText(@NonNull Text text) {
        Set<String> candidates = new LinkedHashSet<>();
        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                String value = normalize(line.getText());
                if (ADDRESS_PATTERN.matcher(value).find()) candidates.add(value);
            }
        }
        if (candidates.isEmpty()) {
            scanButton.setEnabled(true);
            status.setText("Ingen tydlig adress hittades. Försök med ett skarpare foto.");
            return;
        }
        status.setText("Hittade " + candidates.size() + " möjliga adresser. Söker koordinater…");
        executor.execute(() -> geocodeAndAppend(new ArrayList<>(candidates)));
    }

    private String normalize(String value) {
        return value.replaceAll("\\s+", " ").replace('–', '-').replace('—', '-').trim();
    }

    private void geocodeAndAppend(List<String> candidates) {
        OsmandApplication app = (OsmandApplication) getApplication();
        GoogleAddressSearchClient client = new GoogleAddressSearchClient(app);
        List<TargetPoint> resolved = new ArrayList<>();
        int failed = 0;
        for (String candidate : candidates) {
            try {
                List<GoogleAddressSearchClient.Result> results = client.search(candidate);
                if (!results.isEmpty()) {
                    GoogleAddressSearchClient.Result r = results.get(0);
                    PointDescription description = new PointDescription(PointDescription.POINT_TYPE_ADDRESS, candidate);
                    resolved.add(new TargetPoint(new LatLon(r.latitude, r.longitude), description, resolved.size()));
                } else failed++;
            } catch (Exception e) {
                failed++;
            }
        }

        if (!resolved.isEmpty()) {
            TargetPointsHelper helper = app.getTargetPointsHelper();
            List<TargetPoint> all = new ArrayList<>(helper.getAllPoints());
            if (helper.getPointToNavigate() != null && !all.isEmpty()) {
                all.addAll(all.size() - 1, resolved);
            } else {
                all.addAll(resolved);
            }
            helper.reorderAllTargetPoints(all, true);
        }

        final int added = resolved.size();
        final int skipped = failed;
        runOnUiThread(() -> {
            scanButton.setEnabled(true);
            status.setText("Klart. Lade till " + added + " adresser i rutten" + (skipped > 0 ? ". " + skipped + " kunde inte hittas." : "."));
            if (added == 0) Toast.makeText(this, "Inga adresser kunde geokodas.", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }
}
