package com.trungpt.example;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class HelloAndroidActivity extends Activity
{
    @ViewById(R.id.btNext)
    Button btNext;

    @AfterViews
    public void afterView()
    {

    }

    @Click(R.id.btNext)
    public void next()
    {
        startActivity(new Intent(getApplicationContext(), Activity2_.class));
    }
}

