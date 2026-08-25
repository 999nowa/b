from pathlib import Path

p = Path('OsmAnd/src/net/osmand/plus/search/QuickSearchHelper.java')
s = p.read_text(encoding='utf-8')
imp = 'import net.osmand.plus.googlemaps.GoogleSearchApi;\n'
marker = 'import net.osmand.plus.OsmandApplication;\n'
if imp not in s:
    if marker not in s:
        raise SystemExit('QuickSearchHelper import anchor not found')
    s = s.replace(marker, marker + imp, 1)
register_anchor = '\t\tcore.registerAPI(new SearchOnlineApi(app));\n'
register_line = register_anchor + '\t\tcore.registerAPI(new GoogleSearchApi(app));\n'
if 'core.registerAPI(new GoogleSearchApi(app));' not in s:
    if register_anchor not in s:
        raise SystemExit('SearchOnlineApi registration anchor not found')
    s = s.replace(register_anchor, register_line, 1)
start = s.find('public static class SearchOnlineApi')
if start < 0:
    raise SystemExit('SearchOnlineApi class not found')
end = s.find('\n\tpublic static class ', start + 1)
if end < 0:
    end = len(s)
block = s[start:end]
s = s[:start] + block + s[end:]
p.write_text(s, encoding='utf-8')
print('GoogleSearchApi registered in QuickSearchHelper')
