package vr.bawei.com.vr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.pano.VrPanoramaEventListener;
import com.google.vr.sdk.widgets.pano.VrPanoramaView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private VrPanoramaView mVrPanoramaView;
    private Bitmap bitmap;
    private myAsyncTask mmyAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //找控件
        mVrPanoramaView = (VrPanoramaView) findViewById(R.id.VrPanoramaView);
        //异步任务
        mmyAsyncTask = new myAsyncTask();
        mmyAsyncTask.execute();
        //隐藏VR效果左下角信息按钮
        mVrPanoramaView.setInfoButtonEnabled(false);
        //隐藏右下角全屏显示按钮
        mVrPanoramaView.setFullscreenButtonEnabled(false);
        //切换VR模式  参数VrWidgetView.DisplayMode.FULLSCREEN_STEREO设备模式 手机横着放试试
        mVrPanoramaView.setDisplayMode(VrWidgetView.DisplayMode.FULLSCREEN_STEREO);
//设置对Vr运行状态的监听
        mVrPanoramaView.setEventListener(new MyVRListener());

    }
    //异步
    private  class myAsyncTask extends AsyncTask<Void,Void,Bitmap>
    {


        @Override
        protected Bitmap doInBackground(Void... params)
        {
            try {
                //从资产目录拿到资源，返回结果是字节流
                InputStream open = getAssets().open("andes.jpg");
                //把字节流转换为bitmap对象
                bitmap = BitmapFactory.decodeStream(open);


            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
                //决定vr是普通效果还是立体效果     默认的是普通效果
            VrPanoramaView.Options options = new VrPanoramaView.Options();
            //立体效果
            options.inputType = VrPanoramaView.Options.TYPE_STEREO_OVER_UNDER;
              mVrPanoramaView.loadImageFromBitmap(bitmap,options);
            super.onPostExecute(bitmap);
        }

    }

    @Override
    //重新获得焦点是获取
    protected void onResume()
    {
        //继续渲染和显示
        mVrPanoramaView.resumeRendering();
        super.onResume();
    }
//失去焦点时回调
    @Override
    protected void onPause()
    {
        //暂停渲染和显示
        mVrPanoramaView.pauseRendering();
        super.onPause();
    }
//Active销毁时回调
    @Override
    protected void onDestroy() {
        //关闭渲染图
        mVrPanoramaView.shutdown();
        if(mmyAsyncTask!=null)
        {
            //在退出activity时如果异步任务没取消，就取消
            if(!mmyAsyncTask.isCancelled())
            {
                mmyAsyncTask.cancel(true);
            }
        }

        super.onDestroy();
    }
    //VR运行状态监听，自定义一个类继承VrPanoramaEventListener，复写两个方法
    private class MyVRListener extends VrPanoramaEventListener
    {
        @Override
        public void onLoadSuccess()
        {
            Toast.makeText(MainActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
            super.onLoadSuccess();
        }

        @Override
        public void onLoadError(String errorMessage)
        {
            Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            super.onLoadError(errorMessage);
        }
    }
}
