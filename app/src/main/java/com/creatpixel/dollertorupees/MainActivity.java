package com.creatpixel.dollertorupees;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    //Creating views
    EditText editTextDollar;
    EditText editTextRupees;
    TextView textViewResultRupees;
    TextView textViewResultDollar;
    CountDownTimer count;
    ProgressBar progressBar;

    //$1 = Rs.74, If we want to see dollar to rupees we need to multiply dollar with rupees with the value of 1Dollar
    //Rs.1 = $0.013, If we want to see rupees to dollar we need to multiply rupees with dollar with the value of 1Rupees

    //LIVE! Variables of Rupees and Dollars
    Double dollarLiveRatesAccordingToRupees; //$0.013 = Rs.1
    Double rupeesLiveRatesAccordingToDollar; //Rs.74 = $1

    int appOpenTest = 0;
    boolean alreadyExecuted = false;
    boolean isAlreadyExecuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //________________________________________________________
        //Getting all views from layout
        final Button buttonGetResult = findViewById(R.id.buttonGetResult);
        progressBar = findViewById(R.id.progressBar);
        editTextDollar = findViewById(R.id.editTextDollar);
        editTextRupees = findViewById(R.id.editTextRupees);
        textViewResultRupees = findViewById(R.id.textViewResultRupees);
        textViewResultDollar = findViewById(R.id.textViewResultDoller);

        //CHeck connection if net is available than, Calling classes and get live data into variables
        checkConnection();

        //Gone and Visible
        buttonGetResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate().alpha(1).setDuration(1000);



        buttonGetResult.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                Log.i("akki", "dollarLiveRatesAccordingToRupees " + dollarLiveRatesAccordingToRupees);
                Log.i("akki", "rupeesLiveRatesAccordingToDollar " + rupeesLiveRatesAccordingToDollar);

                //Check if edit text is empty, If yess than show error message, If no than calculate our result and set in views
                if(editTextRupees.length() == 0){
                    editTextRupees.setError("Please enter rupees");
                }
                else{
                    //Calculating for Rupees to Dollar result
                    double resultOfRToDollar = dollarLiveRatesAccordingToRupees * Double.parseDouble(editTextRupees.getText().toString());
                    textViewResultDollar.setText("Result $"+ Double.toString(resultOfRToDollar));
                }
                if(editTextDollar.length() == 0){
                    editTextDollar.setError("Please enter dollars");
                }
                else{
                    //Calculating for dollar to rupees result
                    double resultOfDollarToR = rupeesLiveRatesAccordingToDollar * Double.parseDouble(editTextDollar.getText().toString());
                    textViewResultRupees.setText("Result ₹ "+ Double.toString(resultOfDollarToR));
                }

                //Button click animation...
                count = new CountDownTimer(1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        buttonGetResult.animate().alpha(0).setDuration(300);
                    }
                    @Override
                    public void onFinish() {
                        buttonGetResult.animate().alpha(1).setDuration(300);
                    }
                }.start();
            }
        });
    }

    public void checkConnection(){
        //Getting button view
        final Button buttonGetResult = findViewById(R.id.buttonGetResult);

        ConnectivityManager manager = (ConnectivityManager)
                getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNet = manager.getActiveNetworkInfo();

        if(null == activeNet){
            Toast.makeText(MainActivity.this, "No internet connection, \n" +
                    "Turn on internet and restart app", Toast.LENGTH_LONG).show();
        }
        else {
            //Calling classes and get live data into variables
            OneDollarIntoRupeesClass oneDollarIntoRupeesClass = new OneDollarIntoRupeesClass();
            OneRupeesIntoDollarClass oneRupeesIntoDollarClass = new OneRupeesIntoDollarClass();
            oneRupeesIntoDollarClass.execute();
            oneDollarIntoRupeesClass.execute();
        }
    }

    //Backround task, new Class for backround task like getting data from website or database
    @SuppressLint("StaticFieldLeak")
    class OneRupeesIntoDollarClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //Storing values that user wanna convert in Double var, Because we wanna calculate
            double doubleRupees = Integer.parseInt(editTextRupees.getText().toString());

            String url = "https://www.google.com/search?rlz=1C1CHBF" +
                    "_enIN846IN847&sxsrf=ALeKk00AyZSNicotm2gcqaRE3va" +
                    "Y8weZkg%3A1596080292835&ei=pEAiX7bMMrOS4-EP6829" +
                    "6Ac&q=rupees+to+dollars+&oq=rupees+to+dollars+&gs" +
                    "_lcp=CgZwc3ktYWIQAzIECCMQJzIECCMQJzIHCAAQFBCHAjIC" +
                    "CAAyAggAMgcIABAUEIcCMgIIADICCAAyAggAMgIIADoECAAQR" +
                    "1DbH1imImC9JWgAcAF4AIAB6gGIAZoEkgEFMC4yLjGYAQCgAQ" +
                    "GqAQdnd3Mtd2l6wAEB&sclient=psy-ab&ved=0ahUKEwi2roy" +
                    "3hvTqAhUzyTgGHetmD30Q4dUDCAw&uact=5";

            //Getting website into Document, Actually we are getting one rupees value to dollar
            Document doc = null;
            try {
                doc = Jsoup.connect(url).get();

                //Getting specific element
                Elements elements = doc.select("div.b1hJbf");
                //Storing value in value String
                String value = elements.text();

                //Now filter only numbers from element value, 1 Indian Rupee equals 0.013 United States Dollar
                String onlyNum = value.replaceAll("[^0-9.]", "");
                //Final filter 10.013 to 0.13
                String oneRupeesInDollar = onlyNum.substring(1, 6);

                return oneRupeesInDollar;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dollarLiveRatesAccordingToRupees = Double.parseDouble(s);

            Button buttonGetResult = findViewById(R.id.buttonGetResult);
            if(appOpenTest < 1) {
                appOpenTest += 1;
            }
            else{
                //Gone and Visible
                buttonGetResult.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                //Show dollar live value once when app open
                if(!alreadyExecuted){
                    Toast.makeText(MainActivity.this, "$" + s, Toast.LENGTH_LONG).show();
                    alreadyExecuted = true;
                }
            }
        }
    }

    //2nd Class
    class OneDollarIntoRupeesClass extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            //Here we are getting HTML data from website, Note:- MAIN AREA
            Document doc = null;
            String url = "https://www.exchangerates.org.uk/Dollars-to-Rupees-currency-conversion-page.html";

            try {
                doc = Jsoup.connect(url).get();
                //Get elements from website
                Elements element = doc.select("#shd2a");
                //Getting element text
                String elementText = element.text();

                //Getting only number from text
                String numOnly = elementText.replaceAll("[^0-9.]", "");
                //Now filter numbers and final number
                String dollarsToRupeesResult = numOnly.substring(1, numOnly.indexOf("."));

                return dollarsToRupeesResult;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            rupeesLiveRatesAccordingToDollar = Double.parseDouble(s);

            Button buttonGetResult = findViewById(R.id.buttonGetResult);
            if(appOpenTest < 1) {
                appOpenTest += 1;
            }
            else{
                //Gone and Visible
                buttonGetResult.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                //Show dollar live value once when app open
                if(!isAlreadyExecuted){
                    Toast.makeText(MainActivity.this, "Rs." + rupeesLiveRatesAccordingToDollar, Toast.LENGTH_SHORT).show();
                    isAlreadyExecuted = true;
                }
            }
        }
    }
}