package com.ylxdzsw.calculator;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.ylxdzsw.kit.R;

enum Status {
    INPUT, RESULT, ERROR
}

public class Calculator extends AppCompatActivity {
    private StringBuilder expression;
    private String result;
    private WebView webView;
    private Status status;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        expression = new StringBuilder();
        webView    = (WebView) findViewById(R.id.webView);
        status     = Status.INPUT;
        result     = "";

        findViewById(R.id.n0).setOnClickListener(v -> appendNumber('0'));
        findViewById(R.id.n1).setOnClickListener(v -> appendNumber('1'));
        findViewById(R.id.n2).setOnClickListener(v -> appendNumber('2'));
        findViewById(R.id.n3).setOnClickListener(v -> appendNumber('3'));
        findViewById(R.id.n4).setOnClickListener(v -> appendNumber('4'));
        findViewById(R.id.n5).setOnClickListener(v -> appendNumber('5'));
        findViewById(R.id.n6).setOnClickListener(v -> appendNumber('6'));
        findViewById(R.id.n7).setOnClickListener(v -> appendNumber('7'));
        findViewById(R.id.n8).setOnClickListener(v -> appendNumber('8'));
        findViewById(R.id.n9).setOnClickListener(v -> appendNumber('9'));

        findViewById(R.id.lp)   .setOnClickListener(v -> appendNumber('('));
        findViewById(R.id.rp)   .setOnClickListener(v -> appendNumber(')'));
        findViewById(R.id.dot)  .setOnClickListener(v -> appendNumber('.'));

        findViewById(R.id.plus) .setOnClickListener(v -> appendOperator('+'));
        findViewById(R.id.minus).setOnClickListener(v -> appendOperator('-'));
        findViewById(R.id.mul)  .setOnClickListener(v -> appendOperator('*'));
        findViewById(R.id.div)  .setOnClickListener(v -> appendOperator('/'));

        findViewById(R.id.ac)  .setOnClickListener(v -> clearAll());
        findViewById(R.id.back).setOnClickListener(v -> deleteLastOne());
        findViewById(R.id.eq)  .setOnClickListener(v -> calculate());
        findViewById(R.id.eq)  .setOnLongClickListener(v -> ans());

        webView.loadUrl("file:///android_asset/www/screen.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void appendNumber(char x) {
        switch (status) {
            case ERROR:
            case RESULT:
                clearAll();
            case INPUT:
                expression.append(x);
                break;
        }
        updateView();
    }

    private void appendOperator(char x) {
        switch (status) {
            case ERROR:
                clearAll();
                expression.append(x);
                break;
            case RESULT:
                clearAll();
                expression.append(result).append(x);
                break;
            case INPUT:
                expression.append(x);
                break;
        }
        updateView();
    }

    private boolean ans() {
        if (status != Status.INPUT) {
            clearAll();
        }
        expression.append(result);
        updateView();
        return true;
    }

    private void deleteLastOne() {
        int l = expression.length();
        if (l > 0) {
            expression.deleteCharAt(l - 1);
        }
        status = Status.INPUT;
        updateView();
    }

    private void clearAll() {
        expression.delete(0, expression.length());
        status = Status.INPUT;
        updateView();
    }

    private void calculate() {
        if (status != Status.INPUT) {
            return;
        }

        webView.evaluateJavascript("calculate('"+this.expression+"');", val -> {
            val = val.substring(1, val.length()-1);

            if (val.equals("error")) {
                status = Status.ERROR;
            } else {
                status = Status.RESULT;
                result = val;
            }

            updateView();
        });
    }

    private void updateView() {
        switch (status) {
            case ERROR:
                webView.evaluateJavascript("setContent('error');", val -> {});
                break;
            case RESULT:
                webView.evaluateJavascript("setContent('"+expression+"',"+result+");", val -> {});
                break;
            case INPUT:
                webView.evaluateJavascript("setContent('"+expression+"');", val -> {});
                break;
        }
    }

}
