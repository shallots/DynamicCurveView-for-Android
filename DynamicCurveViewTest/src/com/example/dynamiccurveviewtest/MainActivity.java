package com.example.dynamiccurveviewtest;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hyw.dynamic.curveview.CurveDataRefreshThread;
import com.hyw.dynamic.curveview.CurveDataRefreshThread.ViewHandler;
import com.hyw.dynamic.curveview.DataPoint;
import com.hyw.dynamic.curveview.DynamicCurveView;

public class MainActivity extends ActionBarActivity {

	private DynamicCurveView dynamicCurveView;
	private Button gen;
	private Button stop;

	private CurveDataRefreshThread curveDataRefreshThread;
	private ViewHandler viewHandler;

	private Thread genThread;
	private boolean stopgen = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();
	}

	private void initViews() {
		dynamicCurveView = (DynamicCurveView) findViewById(R.id.testdcv);
		gen = (Button) findViewById(R.id.generatePoints);
		stop = (Button) findViewById(R.id.stopDynamicCurve);
	}

	public void genPoints(View v) {
		if(genThread!=null)
			return;
		genThread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (curveDataRefreshThread == null) {
					curveDataRefreshThread = new CurveDataRefreshThread(
							dynamicCurveView);
					curveDataRefreshThread.start();
				}
				float factor = 0;
				stopgen = false;
				while (!stopgen) {
					DataPoint dataPoint = new DataPoint(1,
							(float) Math.sin(factor * Math.PI)*100);
					
//					DataPoint dataPoint = new DataPoint(1, new Random().nextFloat()*100);
					if (curveDataRefreshThread.getViewHandler() != null)
						curveDataRefreshThread.getViewHandler()
								.obtainMessage(100, dataPoint).sendToTarget();
					try {
						Thread.sleep(25);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (factor > 10000)
						break;
					factor += 0.1;
				}
			}
		});
		genThread.start();
	}

	public void stopGen(View v) {
		stopgen = true;
		if (genThread != null){
			genThread.interrupt();
			genThread = null;
		}
		if (curveDataRefreshThread != null) {
			curveDataRefreshThread.exit();
			curveDataRefreshThread = null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
