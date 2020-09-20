
package com.example.MA02_20150253;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    public static final String TAG = "SearchActivity";

    EditText etSearch;
    ListView listView;
    String apiAddress;
    String query;

    SearchAdapter adapter;
    ArrayList<MySearchDto> resultList;
    SearchXmlParser parser;

    MySearchDBHelper mySearchDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = (EditText) findViewById(R.id.etSearch);
        listView = (ListView) findViewById(R.id.searchListView);

        resultList = new ArrayList<>();
        adapter = new SearchAdapter(this, R.layout.activity_search_result, resultList);
        listView.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.encyc_api_url);
        parser = new SearchXmlParser();

        mySearchDBHelper = new MySearchDBHelper(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(resultList.get(position).getLink()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(SearchActivity.this, "연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(SearchActivity.this)
                        .setTitle("검색 결과 저장")
                        .setMessage("찾은 정보를 저장하시겠습니까?")
                        .setPositiveButton("저장",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                //정보를 DB에 저장
                                SQLiteDatabase db = mySearchDBHelper.getWritableDatabase();
                                ContentValues row = new ContentValues();
                                row.put("title", resultList.get(position).getTitle());
                                row.put("description", resultList.get(position).getDescription());
                                row.put("link", resultList.get(position).getLink());
                                long result = db.insert(MySearchDBHelper.SEARCH_TABLE_NAME, null, row);
                                if (result > 0) Toast.makeText(SearchActivity.this, "추가했습니다.", Toast.LENGTH_SHORT).show();
                                mySearchDBHelper.close();
                            }
                        })
                        .setNegativeButton("취소", null).show();
                return true;
            }
        });
    }


    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearch:
                query = etSearch.getText().toString();
                new NaverAsyncTask().execute();
                break;
            case R.id.imgBtnGoList:
                Intent intent = new Intent(this, MySearchListViewActivity.class);
                startActivity(intent);
        }
    }


    class NaverAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(SearchActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer response = new StringBuffer();

            // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
            String clientId = getResources().getString(R.string.client_id);
            String clientSecret = getResources().getString(R.string.client_secret);

            try {
                String apiURL = apiAddress + URLEncoder.encode(query, "UTF-8");
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // response 수신
                int responseCode = con.getResponseCode();
                if (responseCode==200) {
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                } else {
                    Log.e(TAG, "API 호출 에러 발생 : 에러코드=" + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, result);

            resultList = parser.parse(result);
            adapter.setList(resultList);
            adapter.notifyDataSetChanged();

            progressDlg.dismiss();
        }
    }

}
