package net.osmand.plus.googlemaps;

import androidx.annotation.NonNull;

import net.osmand.data.LatLon;
import net.osmand.plus.OsmandApplication;
import net.osmand.search.core.ObjectType;
import net.osmand.search.core.SearchCoreFactory.SearchBaseAPI;
import net.osmand.search.core.SearchPhrase;
import net.osmand.search.core.SearchResult;
import net.osmand.search.SearchUICore.SearchResultMatcher;

import java.io.IOException;
import java.util.List;

/** Bridges Google Places results into OsmAnd's normal SearchResult stream. */
public final class GoogleSearchApi extends SearchBaseAPI {
    public static final int PRIORITY = 700;
    private final OsmandApplication app;

    public GoogleSearchApi(@NonNull OsmandApplication app) {
        super(ObjectType.ONLINE_SEARCH);
        this.app = app;
    }

    @Override
    public boolean isSearchMoreAvailable(SearchPhrase phrase) { return false; }

    @Override
    public boolean search(SearchPhrase phrase, SearchResultMatcher matcher) throws IOException {
        if (!GoogleSearchPreferences.isGoogleSearchEnabled(app) || phrase.isEmpty()) return false;
        String key = GoogleMapsPreferences.getApiKey(app);
        if (key == null || key.trim().isEmpty()) return false;
        try {
            List<GooglePlacesSearchProvider.Result> results = GooglePlacesSearchProvider.search(app, phrase.getFullSearchPhrase());
            for (GooglePlacesSearchProvider.Result result : results) {
                SearchResult sr = new SearchResult(phrase);
                sr.localeName = result.name;
                sr.localeRelatedObjectName = result.address;
                sr.location = new LatLon(result.latitude, result.longitude);
                sr.objectType = ObjectType.ONLINE_SEARCH;
                sr.object = result.placeId;
                sr.priority = PRIORITY;
                sr.preferredZoom = 17;
                matcher.publish(sr);
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public int getSearchPriority(SearchPhrase phrase) {
        String key = GoogleMapsPreferences.getApiKey(app);
        if (!GoogleSearchPreferences.isGoogleSearchEnabled(app) || key == null || key.trim().isEmpty() || phrase.isEmpty()) return -1;
        return PRIORITY;
    }
}
