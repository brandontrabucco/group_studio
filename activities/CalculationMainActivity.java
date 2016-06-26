package com.brandon.apps.groupstudio.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.brandon.apps.groupstudio.assets.BaseActivity;
import com.brandon.apps.groupstudio.assets.Calculation;
import com.brandon.apps.groupstudio.inflaters.CalculationListInflater;
import com.brandon.apps.groupstudio.assets.DatabaseAdapter;
import com.brandon.apps.groupstudio.R;
import com.brandon.apps.groupstudio.assets.ResultCode;


public class CalculationMainActivity extends BaseActivity {

    private ListView calculationListView;
    private ImageButton newButton;
    private CalculationListInflater calculationListInflater;
    private int typeId;
    private Intent intent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button_overlay_activity);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        intent = getIntent();
        String name = intent.getStringExtra("name");
        typeId = intent.getIntExtra("id",0);

        calculationListView = (ListView) findViewById(R.id.object_list);
        newButton = (ImageButton) findViewById(R.id.new_button);
        calculationListInflater = new CalculationListInflater(CalculationMainActivity.this, calculationListView, typeId);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculationMainActivity.this, CreateCalculationActivity.class);
                intent.putExtra("id", typeId);
                startActivityForResult(intent, ResultCode.CREATE_CODE);
            }
        });

        calculationListInflater.populateList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED){
            calculationListInflater.populateList();
            return;
        }

        switch (requestCode){
            case ResultCode.CREATE_CODE:
                Calculation calculation = new Calculation(0, data.getIntExtra("target",0), data.getIntExtra("stat",0), data.getStringExtra("name"));
                database.open();
                long id = database.insertCalculation(typeId, calculation);
                database.close();
                calculation.setId((int) id);
                if (id < 0)
                    Toast.makeText(getBaseContext(), "An error has occurred!", Toast.LENGTH_SHORT).show();
                calculationListInflater.populateList();
                Toast.makeText(getBaseContext(), calculation.getName() + " has been added to your Calculations!", Toast.LENGTH_SHORT).show();
                break;
            case ResultCode.UPDATE_CODE:
                Toast.makeText(getBaseContext(), data.getStringExtra("name") + " has been updated!", Toast.LENGTH_SHORT).show();
                calculationListInflater.populateList();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_back) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
