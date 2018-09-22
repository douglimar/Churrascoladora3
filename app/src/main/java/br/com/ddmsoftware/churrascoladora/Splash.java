package br.com.ddmsoftware.churrascoladora;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;


public class Splash extends Activity implements Runnable {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_splash);
        
        Handler h = new Handler();
        
        h.postDelayed(this,2000);
        
    }
    
	public void run() {
		// TODO Auto-generated method stub		
    	
    	startActivity(new Intent(this, MainActivity.class ));
    	finish();		
	}    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_splash, menu);
        return true;
    }    
}
