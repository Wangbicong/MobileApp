package com.ylxdzsw.calculator;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.ylxdzsw.calculator.R;

enum Status {
    INPUT, RESULT, ERROR
}

public class Calculator extends AppCompatActivity {
    private StringBuilder expression;
    private float result;
    private WebView webView;
    private Status status;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        expression = new StringBuilder();
        webView = (WebView) findViewById(R.id.webView);
        status = Status.INPUT;

        findViewById(R.id.n0).setOnClickListener(v -> appendNumber(0));
        findViewById(R.id.n1).setOnClickListener(v -> appendNumber(1));
        findViewById(R.id.n2).setOnClickListener(v -> appendNumber(2));
        findViewById(R.id.n3).setOnClickListener(v -> appendNumber(3));
        findViewById(R.id.n4).setOnClickListener(v -> appendNumber(4));
        findViewById(R.id.n5).setOnClickListener(v -> appendNumber(5));
        findViewById(R.id.n6).setOnClickListener(v -> appendNumber(6));
        findViewById(R.id.n7).setOnClickListener(v -> appendNumber(7));
        findViewById(R.id.n8).setOnClickListener(v -> appendNumber(8));
        findViewById(R.id.n9).setOnClickListener(v -> appendNumber(9));

        findViewById(R.id.lp)   .setOnClickListener(v -> appendOperator('('));
        findViewById(R.id.rp)   .setOnClickListener(v -> appendOperator(')'));
        findViewById(R.id.plus) .setOnClickListener(v -> appendOperator('+'));
        findViewById(R.id.minus).setOnClickListener(v -> appendOperator('-'));
        findViewById(R.id.mul)  .setOnClickListener(v -> appendOperator('*'));
        findViewById(R.id.div)  .setOnClickListener(v -> appendOperator('/'));
        findViewById(R.id.dot)  .setOnClickListener(v -> appendOperator('.'));

        findViewById(R.id.ac)  .setOnClickListener(v -> clearAll());
        findViewById(R.id.back).setOnClickListener(v -> deleteLastOne());
        findViewById(R.id.eq)  .setOnClickListener(v -> calculate());

        webView.loadUrl("file:///android_asset/www/screen.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void appendNumber(int x) {
        expression.append(x);
        updateView();
    }

    private void appendOperator(char x) {
        expression.append(x);
        updateView();
    }

    private void deleteLastOne() {
        if (status == Status.INPUT) {
            expression.deleteCharAt(expression.length() - 1);
            updateView();
        } else {
            clearAll();
        }
    }

    private void clearAll() {
        expression.delete(0, expression.length());
        status = Status.INPUT;
        updateView();
    }

    private void calculate() {
        webView.evaluateJavascript("calculate('"+this.expression+"');", val -> Log.wtf("calculator", val));
    }

    private void updateView() {
        webView.evaluateJavascript("setContent('"+this.expression+"');", val -> {});
    }

}
