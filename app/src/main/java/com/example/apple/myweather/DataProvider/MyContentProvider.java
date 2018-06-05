package com.example.apple.myweather.DataProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.apple.myweather.db.City;
import com.example.apple.myweather.db.County;
import com.example.apple.myweather.db.Province;

import org.litepal.crud.DataSupport;

public class MyContentProvider extends ContentProvider {

    public static final int PRO_DIR = 0;
    public static final int PRO_ITEM = 1;
    public static final int CITY_DIR = 2;
    public static final int CITY_ITEM = 3;
    public static final int COUNTY_DIR = 4;
    public static final int COUNTY_ITEM = 5;
    public static final String AUTHORITY = "com.example.apple.myweather.provider";
    private static UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY,"province",PRO_DIR);
        uriMatcher.addURI(AUTHORITY,"province/#",PRO_ITEM);
        uriMatcher.addURI(AUTHORITY,"city",CITY_DIR);
        uriMatcher.addURI(AUTHORITY,"city/#",CITY_ITEM);
        uriMatcher.addURI(AUTHORITY,"county",COUNTY_DIR);
        uriMatcher.addURI(AUTHORITY,"county/#",COUNTY_ITEM);
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        int updateRows = 0;
        switch (uriMatcher.match(uri)){
            case  PRO_DIR :
                updateRows = DataSupport.deleteAll(Province.class,"");
                break;
            case PRO_ITEM :
                String id_province = uri.getPathSegments().get(1);
                updateRows = DataSupport.delete(Province.class,Integer.parseInt(id_province));
                break;
            case CITY_DIR :
                updateRows = DataSupport.deleteAll(City.class,"");
                break;
            case CITY_ITEM :
                String id_city = uri.getPathSegments().get(1);
                updateRows = DataSupport.delete(City.class,Integer.parseInt(id_city));
                break;
            case COUNTY_DIR :
                updateRows = DataSupport.deleteAll(County.class,"");
                break;
            case COUNTY_ITEM :
                String id_county = uri.getPathSegments().get(1);
                updateRows = DataSupport.delete(County.class,Integer.parseInt(id_county));
                break;
            default:
                break;
        }
        return updateRows;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)){
            case PRO_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.apple.myweather.provider.province";
            case PRO_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.apple.myweather.provider.province";
            case CITY_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.apple.myweather.provider.city";
            case CITY_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.apple.myweather.provider.city";
            case COUNTY_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.apple.myweather.provider.county";
            case COUNTY_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.apple.myweather.provider.county";

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        Uri uriReturn = null;
        switch (uriMatcher.match(uri)){
            case PRO_DIR:
            case PRO_ITEM:
                int ProvinceId = (int)values.get("id");
                String ProvinceName = (String)values.get("provinceName");
                int ProvinceCode = (int)values.get("provinceCode");
                Province province = new Province();
                province.setProvinceCode(ProvinceCode);
                province.setProvinceName(ProvinceName);
                province.setId(ProvinceId);
                province.save();
                uriReturn = Uri.parse("content://" + AUTHORITY + "/province/" + ProvinceId);
                break;
            case CITY_DIR:
            case CITY_ITEM:
                int CityId = (int)values.get("id");
                String CityName = (String)values.get("cityName");
                int CityCode = (int)values.get("cityCode");
                int provinceId = (int)values.get("provinceId");
                City city = new City();
                city.setProvinceId(provinceId);
                city.setCityCode(CityCode);
                city.setCityName(CityName);
                city.setId(CityId);
                city.save();
                uriReturn = Uri.parse("content://" + AUTHORITY + "/city/" + CityId);
                break;
            case COUNTY_DIR:
            case COUNTY_ITEM:
                int CountyId = (int)values.get("id");
                int cityid = (int)values.get("cityId");
                String WeatherId = (String)values.get("weatherId");
                String CountyName = (String)values.get("countyName");
                County county = new County();
                county.setWeatherId(WeatherId);
                county.setCityId(cityid);
                county.setCountyName(CountyName);
                county.setId(CountyId);
                county.save();
                uriReturn = Uri.parse("content://" + AUTHORITY + "/county/" + CountyId);
                break;
            default:
                break;
        }
        return uriReturn;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        // TODO: Implement this to handle query requests from clients.
        switch (uriMatcher.match(uri)){
            case  PRO_DIR :
                cursor = DataSupport.findBySQL("select * from Province");
                break;
            case PRO_ITEM :
                String id_province = uri.getPathSegments().get(1);
                cursor = DataSupport.findBySQL("select * from Province where id=?",id_province);
                break;
            case CITY_DIR :
                cursor = DataSupport.findBySQL("select * from City");
                break;
            case CITY_ITEM :
                String id_city = uri.getPathSegments().get(1);
                cursor = DataSupport.findBySQL("select * from City where id=?",id_city);
                break;
            case COUNTY_DIR :
                cursor = DataSupport.findBySQL("select * from County");
                break;
            case COUNTY_ITEM :
                String id_county = uri.getPathSegments().get(1);
                cursor = DataSupport.findBySQL("select * from County where id=?",id_county);
                break;
            default:
                break;
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int updateRows = 0;
        switch (uriMatcher.match(uri)){
            case  PRO_DIR :
                updateRows = DataSupport.updateAll(Province.class,values);
                break;
            case PRO_ITEM :
                String id_province = uri.getPathSegments().get(1);
                updateRows = DataSupport.update(Province.class,values,Integer.parseInt(id_province));
                break;
            case CITY_DIR :
                updateRows = DataSupport.updateAll(City.class,values);
                break;
            case CITY_ITEM :
                String id_city = uri.getPathSegments().get(1);
                updateRows = DataSupport.update(City.class,values,Integer.parseInt(id_city));
                break;
            case COUNTY_DIR :
                updateRows = DataSupport.updateAll(City.class,values);
                break;
            case COUNTY_ITEM :
                String id_county = uri.getPathSegments().get(1);
                updateRows = DataSupport.update(County.class,values,Integer.parseInt(id_county));
                break;
            default:
                break;
        }
        return updateRows;
    }
}
