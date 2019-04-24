package com.heartrate.heartratemonitor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import com.heartrate.heartratemonitor.ImageProcessing;

public class CameraPreview extends Activity {
    //曲线
    private Timer timer = new Timer();
    //Timer任务，与Timer配套使用
    private TimerTask task;
    private static int gx;
    private static int j;

    private static double flag = 1;
    private Handler handler;
    private String title = "pulse";
    private XYSeries series;
    private XYMultipleSeriesDataset mDataset;
    private GraphicalView chart;
    private XYMultipleSeriesRenderer renderer;
    private Context context;
    private int addX = -1;
    double addY;
    int[] xv = new int[1000];
    int[] yv = new int[1000];
    int[] hua=new int[]{9,10,11,12,13,14,13,12,11,10,9,8,7,6,7,8,9,10,11,10,10};

    private static final AtomicBoolean processing = new AtomicBoolean(false);
    //Android Phone preview control
    private static SurfaceView preview = null;
    //Preview settings information
    private static SurfaceHolder previewHolder = null;
    //Android Phone camera handle
    private static Camera camera = null;
    //private static View image = null;
    public static TextView mTV_Heart_Rate = null;
    public static TextView mTV_Heart_Rate20 = null;
    private static TextView mTV_Avg_Pixel_Values = null;
    private static TextView mTV_pulse = null;
    private static PowerManager.WakeLock wakeLock = null;
    private static int averageIndex = 0;
    private static final int averageArraySize = 11;
    private static final int[] averageArray = new int[averageArraySize];

    /**
     * Type enumeration
     */
    public static enum TYPE {
        GREEN, RED
    };

    //Set default type
    private static TYPE currentType = TYPE.GREEN;
    //Get current type
    public static TYPE getCurrent() {
        return currentType;
    }
    //Heartbeat subscript value
    private static int beatsIndex = 0;
    //Heartbeat array size
    private static final int beatsArraySize = 1;
    //心跳数组
    private static final int[] beatsArray = new int[beatsArraySize];
    //心跳脉冲
    private static double beats = 0;
    //开始时间
    private static long startTime = 0;
    private static double HR20 = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);

        initConfig();
    }

    /**
     * 初始化配置
     */
    @SuppressWarnings("deprecation")
    private void initConfig() {
        //曲线
        context = getApplicationContext();

        //这里获得main界面上的布局，下面会把图表画在这个布局里面
        LinearLayout layout = (LinearLayout)findViewById(R.id.id_linearLayout_graph);

        //这个类用来放置曲线上的所有点，是一个点的集合，根据这些点画出曲线
        series = new XYSeries(title);

        //创建一个数据集的实例，这个数据集将被用来创建图表
        mDataset = new XYMultipleSeriesDataset();

        //将点集添加到这个数据集中
        mDataset.addSeries(series);

        //以下都是曲线的样式和属性等等的设置，renderer相当于一个用来给图表做渲染的句柄
        int color = Color.GREEN;
        PointStyle style = PointStyle.CIRCLE;
        renderer = buildRenderer(color, style, true);

        //设置好图表的样式
        setChartSettings(renderer, "X", "Y", 0, 1000, 4,16, Color.WHITE, Color.WHITE);

        //生成图表
        chart = ChartFactory.getLineChartView(context, mDataset, renderer);

        //将图表添加到布局中去
        layout.addView(chart, new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT));

        //这里的Handler实例将配合下面的Timer实例，完成定时更新图表的功能
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //刷新图表
                updateChart();
                super.handleMessage(msg);
            }
        };

        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        timer.schedule(task, 1,20);           //曲线
        //获取SurfaceView控件
        preview = (SurfaceView) findViewById(R.id.id_preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mTV_Heart_Rate = (TextView) findViewById(R.id.id_tv_heart_rate);
        mTV_Avg_Pixel_Values = (TextView) findViewById(R.id.id_tv_Avg_Pixel_Values);
        mTV_pulse = (TextView) findViewById(R.id.id_tv_pulse);
        mTV_Heart_Rate20 = (TextView) findViewById(R.id.id_tv_heart_rate20);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,  "My Tag");
    }

    //	曲线
    @Override
    public void onDestroy() {
        //当结束程序时关掉Timer
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    /**
     * 创建图表
     */
    protected XYMultipleSeriesRenderer buildRenderer(int color, PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        //Atur gaya kurva itu sendiri dalam bagan, termasuk warna, ukuran titik, dan ketebalan garis.
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.RED);
        r.setLineWidth(1);
        renderer.addSeriesRenderer(r);
        return renderer;
    }

    /**
     * 设置图标的样式
     * @param renderer
     * @param xTitle：x标题
     * @param yTitle：y标题
     * @param xMin：x最小长度
     * @param xMax：x最大长度
     * @param yMin:y最小长度
     * @param yMax：y最大长度
     * @param axesColor：颜色
     * @param labelsColor：标签
     */
    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String xTitle, String yTitle,
                                    double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        //Untuk render bagan, lihat dokumentasi api.
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.GREEN);
        renderer.setXLabels(20);
        renderer.setYLabels(10);
        renderer.setXTitle("Time");
        renderer.setYTitle("mmHg");
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setPointSize((float) 3 );
        renderer.setShowLegend(false);
    }

    /**
     * Perbarui informasi ikon
     */
    private void updateChart() {
        //Atur simpul berikutnya yang akan ditambahkan
        if(flag == 1) {
            addY = 10;
        }
        else {
            flag = 1;
            if(gx < 200){
                if(hua[20] > 1){
                    Toast.makeText(CameraPreview.this, "Please cover the camera lens with your fingertips!", Toast.LENGTH_SHORT).show();
                    hua[20] = 0;
                }
                hua[20]++;
                return;
            }
            else {
                hua[20] = 10;
            }
            j = 0;
        }
        if(j < 20){
            addY=hua[j];
            j++;
        }

        //Hapus set poin lama dari dataset
        mDataset.removeSeries(series);

        //Tentukan berapa banyak poin dalam set titik saat ini, karena layar hanya dapat menampung 100 total, jadi ketika jumlah poin melebihi 100, panjangnya selalu 100.
        int length = series.getItemCount();
        int bz = 0;
        //addX = length;
        if (length > 1000) {
            //wakeLock.release();
            length = 1000;
            bz=1;
        }
        addX = length;
        //Ambil nilai-nilai dari titik lama atur x dan y ke dalam cadangan, dan tambah nilai x dengan 1, menyebabkan kurva bergerak ke kanan.
        for (int i = 0; i < length; i++) {
            xv[i] = (int) series.getX(i) - bz;
            yv[i] = (int) series.getY(i);
        }

        //Set point dihapus terlebih dahulu, siap untuk membuat set point baru
        series.clear();
        mDataset.addSeries(series);
        // Tambahkan poin yang baru dihasilkan ke set poin pertama, lalu tambahkan kembali serangkaian poin setelah transformasi koordinat ke titik yang ditetapkan dalam loop body
        // Di sini Anda dapat menguji efek membalik urutan, yaitu, pertama jalankan loop body, lalu tambahkan titik yang baru dibuat
        series.add(addX, addY);
        for (int k = 0; k < length; k++) {
            series.add(xv[k], yv[k]);
        }
        //Tambahkan set poin baru di dataset
        //mDataset.addSeries(series);

        //Lihat pembaruan, tanpa langkah ini, kurva tidak akan membuat dinamis
        //Jika di utas utama non-UI, Anda perlu memanggil postInvalidate (), api referensi khusus
        chart.invalidate();
    } //曲线


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        //wakeLock.acquire();
        camera = Camera.open();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onPause() {
        super.onPause();
        wakeLock.release();
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    /**
     * 相机预览方法
     * 这个方法中实现动态更新界面UI的功能，
     * 通过获取手机摄像头的参数来实时动态计算平均像素值、脉冲数，从而实时动态计算心率值。
     */
    private static android.hardware.Camera.PreviewCallback previewCallback = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) {
                throw new NullPointerException();
            }
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) {
                throw new NullPointerException();
            }
            if (!processing.compareAndSet(false, true)) {
                return;
            }
            int width = size.width;
            int height = size.height;

            //Image Processing
            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(),height,width);
            gx = imgAvg;
            mTV_Avg_Pixel_Values.setText("The average pixel value is" + String.valueOf(imgAvg));
            Log.d("ImgAvg ", String.valueOf(imgAvg));
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }
            //Hitung rata-rata
            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            //Hitung rata-rata
            int rollingAverage = (averageArrayCnt > 0)?(averageArrayAvg/averageArrayCnt):0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    flag=0;
                    mTV_pulse.setText("The number of pulses is" + String.valueOf(beats));
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if(averageIndex == averageArraySize) {
                averageIndex = 0;
            }
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            if (newType != currentType) {
                currentType = newType;
            }

            //Dapatkan waktu akhir sistem (ms)
            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if(totalTimeInSecs >=1){
                mTV_Heart_Rate.setText("Timers "+String.valueOf(totalTimeInSecs));
            }
            if (totalTimeInSecs >= 20) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180|| imgAvg < 200) {
                    //获取系统开始时间（ms）
                    startTime = System.currentTimeMillis();
                    //beats心跳总数
                    beats = 0;
                    processing.set(false);
                    return;
                }

                if(beatsIndex == beatsArraySize) {
                    beatsIndex = 0;
                }
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                /*mTV_Heart_Rate.setText("Heart Rate"+String.valueOf(beatsAvg) +
                        "  value:" + String.valueOf(beatsArray.length) +
                        "    " + String.valueOf(beatsIndex) +
                        "    " + String.valueOf(beatsArrayAvg) +
                        "    " + String.valueOf(beatsArrayCnt));*/
                if(beatsIndex == beatsArray.length){ HR20 = beatsAvg;}
                mTV_Heart_Rate20.setText("Heart Rate every 20 seconds "+String.valueOf(HR20));
                //获取系统时间（ms）
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };



    /**
     * 预览回调接口
     */
    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        //创建时调用
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo","Exception in setPreviewDisplay()", t);
            }
        }

        //当预览改变的时候回调此方法
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        //销毁的时候调用
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    /**
     * 获取相机最小的预览尺寸
     */
    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                }
                else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;
                    if (newArea < resultArea) {
                        result = size;
                    }
                }
            }
        }
        return result;
    }
}