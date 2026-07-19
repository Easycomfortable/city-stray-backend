package com.citystray.controller;

import com.citystray.common.Result;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * 地图服务接口
 * 1. 先用内置城市坐标库粗定位（省+市，无需网络）
 * 2. 再尝试调外部API获取精确地址（直连/代理两种方式）
 */
@RestController
@RequestMapping("/api/map")
public class MapController {

    private static final Proxy NO_PROXY = Proxy.NO_PROXY;
    private static final Proxy SYS_PROXY = new Proxy(Proxy.Type.HTTP,
            new InetSocketAddress("127.0.0.1", 7890));

    // 主要城市坐标库：[城市名, 纬度, 经度, 省]
    private static final String[][] CITIES = {
        {"郑州市", "34.75", "113.65", "河南省"},
        {"开封市", "34.79", "114.35", "河南省"},
        {"洛阳市", "34.62", "112.45", "河南省"},
        {"新乡市", "35.30", "113.87", "河南省"},
        {"北京市", "39.90", "116.40", "北京市"},
        {"上海市", "31.23", "121.47", "上海市"},
        {"广州市", "23.13", "113.26", "广东省"},
        {"深圳市", "22.54", "114.06", "广东省"},
        {"成都市", "30.57", "104.07", "四川省"},
        {"武汉市", "30.59", "114.31", "湖北省"},
        {"杭州市", "30.27", "120.15", "浙江省"},
        {"南京市", "32.06", "118.78", "江苏省"},
        {"天津市", "39.13", "117.20", "天津市"},
        {"重庆市", "29.56", "106.55", "重庆市"},
        {"西安市", "34.26", "108.94", "陕西省"},
        {"长沙市", "28.23", "112.94", "湖南省"},
        {"青岛市", "36.07", "120.38", "山东省"},
        {"济南市", "36.65", "117.00", "山东省"},
        {"沈阳市", "41.80", "123.43", "辽宁省"},
        {"哈尔滨市", "45.75", "126.65", "黑龙江省"},
        {"长春市", "43.88", "125.32", "吉林省"},
        {"大连市", "38.91", "121.60", "辽宁省"},
        {"石家庄市", "38.04", "114.51", "河北省"},
        {"太原市", "37.87", "112.55", "山西省"},
        {"合肥市", "31.82", "117.23", "安徽省"},
        {"福州市", "26.07", "119.30", "福建省"},
        {"厦门市", "24.48", "118.09", "福建省"},
        {"昆明市", "25.04", "102.71", "云南省"},
        {"贵阳市", "26.65", "106.63", "贵州省"},
        {"南宁市", "22.82", "108.37", "广西壮族自治区"},
        {"兰州市", "36.06", "103.83", "甘肃省"},
        {"呼和浩特市", "40.84", "111.75", "内蒙古自治区"},
        {"乌鲁木齐市", "43.83", "87.62", "新疆维吾尔自治区"},
        {"拉萨市", "29.65", "91.13", "西藏自治区"},
        {"银川市", "38.49", "106.27", "宁夏回族自治区"},
        {"西宁市", "36.62", "101.78", "青海省"},
        {"海口市", "20.02", "110.35", "海南省"},
        {"苏州市", "31.30", "120.62", "江苏省"},
        {"无锡市", "31.57", "120.30", "江苏省"},
        {"温州市", "28.00", "120.67", "浙江省"},
        {"宁波市", "29.87", "121.55", "浙江省"},
        {"珠海市", "22.27", "113.58", "广东省"},
        {"东莞市", "23.04", "113.74", "广东省"},
        {"佛山市", "23.02", "113.12", "广东省"},
    };

    @GetMapping("/reverse-geocode")
    public Result<?> reverseGeocode(@RequestParam double lat, @RequestParam double lng) {
        String address = null;

        // 1. 尝试外部API：Nominatim直连
        if (address == null) address = tryNominatim(lat, lng, NO_PROXY);
        // 2. Nominatim走代理
        if (address == null) address = tryNominatim(lat, lng, SYS_PROXY);
        // 3. Tianditu直连
        if (address == null) address = tryTianditu(lat, lng, NO_PROXY);
        // 4. Tianditu走代理
        if (address == null) address = tryTianditu(lat, lng, SYS_PROXY);

        // 5. 最终fallback：内置城市坐标库
        if (address == null) {
            address = matchCity(lat, lng);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("address", address);
        return Result.success(data);
    }

    /**
     * 根据坐标匹配最近城市（简单欧氏距离）
     */
    private String matchCity(double lat, double lng) {
        String best = null;
        double minDist = Double.MAX_VALUE;
        for (String[] city : CITIES) {
            double cLat = Double.parseDouble(city[1]);
            double cLng = Double.parseDouble(city[2]);
            double dist = Math.sqrt(Math.pow(lat - cLat, 2) + Math.pow(lng - cLng, 2));
            if (dist < minDist) {
                minDist = dist;
                best = city[3] + city[0];
            }
        }
        // 如果最近城市超过200km（约2度），显示粗略定位
        if (minDist > 2.0) {
            return String.format("约%.2f°N, %.2f°E", lat, lng);
        }
        return best + "附近";
    }

    private String tryNominatim(double lat, double lng, Proxy proxy) {
        try {
            String urlStr = "https://nominatim.openstreetmap.org/reverse?format=json"
                    + "&lat=" + lat + "&lon=" + lng
                    + "&accept-language=zh-CN";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "CityStrayApp/1.0");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            String result = readResponse(conn);
            conn.disconnect();
            return extractJsonField(result, "display_name");
        } catch (Exception e) {
            return null;
        }
    }

    private String tryTianditu(double lat, double lng, Proxy proxy) {
        try {
            String key = "0d38fb2572a4c075c719d3c739731dc0";
            String postStr = java.net.URLEncoder.encode(
                    "{\"lon\":" + lng + ",\"lat\":" + lat + ",\"ver\":1}", "UTF-8");
            String urlStr = "https://api.tianditu.gov.cn/geocoder?postStr=" + postStr
                    + "&type=geocode&tk=" + key;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(4000);
            String result = readResponse(conn);
            conn.disconnect();
            if (result.contains("权限类型错误") || result.contains("301012")) return null;
            return extractJsonField(result, "formatted_address");
        } catch (Exception e) {
            return null;
        }
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    private String extractJsonField(String json, String field) {
        String search = "\"" + field + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end <= start) return null;
        return json.substring(start, end);
    }
}
