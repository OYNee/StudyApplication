package com.example.MA02_20150253;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MannerLocationSearchActivity extends AppCompatActivity{
    public static final String TAG = "MainActivity";

    EditText etTarget;
    ListView lvList;
    String apiAddress;  //사용할 openAPI 주소.

    String query;   //검색 키워드.

    MannerLocAdapter adapter;
    ArrayList<MyLocation> resultList;
    MannerLocXmlParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_search);

        etTarget = (EditText)findViewById(R.id.etTarget);
        lvList = (ListView)findViewById(R.id.lvList);

        resultList = new ArrayList();
        adapter = new MannerLocAdapter(this, R.layout.loc_item, resultList);
        lvList.setAdapter(adapter);     //처음 연결하면 데이터가 들어있지 않기 때문에 빈 화면 출력

        apiAddress = getResources().getString(R.string.local_api_url);
        parser = new MannerLocXmlParser();

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("Addr", resultList.get(position).getAddress());
                Log.e("TAG", resultList.get(position).getAddress());
                setResult(RESULT_OK, intent);
                finish();


            }
        });
    }




    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearch:
                query = etTarget.getText().toString();  //사용자가 입력한 문자열을 읽어서 query에 넣음.
                new NaverAsyncTask().execute();     //AsyncTask 실행
                break;
        }
    }


    class NaverAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {     //작업 수행 전에 할 작업
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MannerLocationSearchActivity.this, "Wait", "Downloading...");     //검색 중에 띄울 메세지
        }

        @Override
        protected String doInBackground(String... strings) {        //백그라운드에서 수행할 작업(비동기 방식)
            StringBuffer response = new StringBuffer();     //String Buffer 생성

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
                int responseCode = con.getResponseCode();   //응답 결과를 받아 온다.
                if (responseCode==200) {            //코드가 무사히 전달되었으면 responseCode==200, 입력 스트림으로 xml 문서를 읽어들인다.
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);     //줄단위로 저장해 나간다.
                    }
                    in.close();
                } else {        //응답 결과가 잘못된 경우
                    Log.e(TAG, "API 호출 에러 발생 : 에러코드=" + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response.toString();
        }


        @Override
        protected void onPostExecute(String result) {       //백그라운드 작업 결과가 매개변수로 들어온다.
            Log.i(TAG, result);

            resultList = parser.parse(result);      //결과물을 파싱하여 resultList에 저장. 이후 연결 및 변화된 데이터로 리스트 화면에 띄우기
            adapter.setList(resultList);
            adapter.notifyDataSetChanged();

            progressDlg.dismiss();
        }
    }

}
