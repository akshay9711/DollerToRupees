package com.creatpixel.dollertorupees;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //________________________________________________________
        //Getting all views from layout
        Button buttonGetResult = findViewById(R.id.buttonGetResult);
        editTextDollar = findViewById(R.id.editTextDollar);
        editTextRupees = findViewById(R.id.editTextRupees);
        textViewResultRupees = findViewById(R.id.textViewResultRupees);
        textViewResultDollar = findViewById(R.id.textViewResultDoller);

        buttonGetResult.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                //Check if edit text is emty, If yess than show error message, If no than call class that show result we want
                if(editTextRupees.length() == 0){
                    editTextRupees.setError("Enter rupees");
                }
                else{
                    //Calling class for Rupees to Doller result
                    OneRupeesIntoDollarClass rupeesIntoDollerClass = new OneRupeesIntoDollarClass();
                    rupeesIntoDollerClass.execute();
                }
                if(editTextDollar.length() == 0){
                    editTextDollar.setError("Enter dollar");
                }
                else{
                    //Calling class for dollar to rupees result
                    OneDollarIntoRupeesClass dollarIntoRupeesClass = new OneDollarIntoRupeesClass();
                    dollarIntoRupeesClass.execute();
                }
            }
        });
    }

    //Backround task, new Class for backround task like getting data from website or database
    @SuppressLint("StaticFieldLeak")
    class OneRupeesIntoDollarClass extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //Storing values that user wanna convert in Double var, Because we wanna calculate
            //double doubleDoller = Integer.parseInt(editTextDoller.getText().toString());
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

            //Getting website into Document, Actually we are getting one rupees value to doller
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
                String oneRupeesInDoller = onlyNum.substring(1, 6);

                //Now converting values here
                double doubleResultRupees = doubleRupees*Double.parseDouble(oneRupeesInDoller);
                String rupeesResult = Double.toString(doubleResultRupees);

                //Returning result to OnPostExecute
                return rupeesResult;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //Finally!! Pasting our result into textview
            textViewResultDollar.setText("$ "+s);
        }
    }

    //2nd Class for Dollar converting
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
            //Finally!! Pasting our result into textview
            double dollar = Double.parseDouble(editTextDollar.getText().toString());
            double dollarResult = dollar*Double.parseDouble(s);
            String dollaResultString = Double.toString(dollarResult);
            textViewResultRupees.setText("₹ "+dollaResultString);
        }
    }
}