package com.trungpt.example;

import android.app.Activity;
import android.os.Bundle;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/**
 * Created by trungpt on 08/01/2015.
 */
@EActivity(R.layout.activity_2)
public class Activity2 extends Activity
{
    @AfterViews
    public void afterView()
    {

    }
}