package com.lex_portfolio.engine.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class StandardAndroidInterface implements AndroidInterface {

    private final Context context;
    private final Handler handler;

    public StandardAndroidInterface(Context context) {
        this.context = context;
        this.handler = new Handler();
    }

    @Override
    public void toast(final String msg, Duration duration) {
        final int durInt;
        switch (duration) {
            case SHORT:
                durInt = Toast.LENGTH_SHORT;
                break;
            case LONG:
                durInt = Toast.LENGTH_LONG;
                break;
            default:
                throw new RuntimeException();
        }
        //TODO: утечка памяти
        //noinspection Convert2Lambda
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, durInt).show();
            }
        });
    }

    @SuppressWarnings("unused")
    @Override
    public void setOrientation(Orientation orientation) {
        throw new RuntimeException("реализация по необходимости");
    }

    public void openPlayMarketPage(String packageName) {
        //TODO: утечка памяти
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String marketAppLink = "market://details?id=" + packageName;
        intent.setData(Uri.parse(marketAppLink));
        context.startActivity(intent);
    }
}
