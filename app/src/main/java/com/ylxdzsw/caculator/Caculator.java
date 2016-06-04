package com.ylxdzsw.caculator;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

enum Status {
    INPUT, RESULT, ERROR
}

public class Caculator extends AppCompatActivity {
    private StringBuilder expression;
    private WebView webView;
    private Status status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caculator);

        webView = (WebView) findViewById(R.id.webView);

        findViewById(R.id.n0).setOnClickListener((e) -> appendNumber(0));
        findViewById(R.id.n1).setOnClickListener((e) -> appendNumber(1));
        findViewById(R.id.n2).setOnClickListener((e) -> appendNumber(2));
        findViewById(R.id.n3).setOnClickListener((e) -> appendNumber(3));
        findViewById(R.id.n4).setOnClickListener((e) -> appendNumber(4));
        findViewById(R.id.n5).setOnClickListener((e) -> appendNumber(5));
        findViewById(R.id.n6).setOnClickListener((e) -> appendNumber(6));
        findViewById(R.id.n7).setOnClickListener((e) -> appendNumber(7));
        findViewById(R.id.n8).setOnClickListener((e) -> appendNumber(8));
        findViewById(R.id.n9).setOnClickListener((e) -> appendNumber(9));

        status = Status.INPUT;
        webView.loadUrl("file:///android_asset/www/screen.html");
        webView.getSettings().setJavaScriptEnabled(true);
    }

    private void appendNumber(int x) {
        expression.append(x);
        updateview();
    }

    private void appendOperator(char x) {
        expression.append(' ')
                  .append(x)
                  .append(' ');
        updateview();
    }

    private void updateView() {
        webView.loadUrl("javascript:setContent('"+this.expression+"');");
    }

    public class JavascriptInterface
    {
        Context context;

        JavascriptInterface(Context c)
        {
            context = c;
        }

        public void showToast(String toast)
        {

        }
    }
}
