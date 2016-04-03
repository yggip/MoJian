package net.roocky.moji.Activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.roocky.moji.BroadcastReceiver.RemindReceiver;
import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.R;
import net.roocky.moji.Util.SoftInput;

import java.sql.Time;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by roocky on 03/28.
 * 查看内容
 */
public class ViewActivity extends AppCompatActivity implements View.OnClickListener,
        DialogInterface.OnClickListener,
        NestedScrollView.OnScrollChangeListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener{
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.fab_edit)
    FloatingActionButton fabEdit;
    @Bind(R.id.tv_remind)
    TextView tvRemind;

    private Intent intent;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private boolean isEdit = false;     //标识当前是否为编辑状态

    private AlertDialog dialogDiary;
    private AlertDialog dialogNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        ButterKnife.bind(this);

        initView();
        setListener();

    }

    private void initView() {
        intent = getIntent();
        databaseHelper = new DatabaseHelper(this, "Moji.db", null, 1);
        database = databaseHelper.getWritableDatabase();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.app_name));
        }
        //显示内容
        tvContent.setText(intent.getStringExtra("content"));
    }

    private void setListener() {
        fabEdit.setOnClickListener(this);
        NestedScrollView scrollView = (NestedScrollView)findViewById(R.id.nsv_content);
        scrollView.setOnScrollChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view, menu);
        if (intent.getStringExtra("from").equals("diary")) {    //日记无需设置提醒
            menu.findItem(R.id.action_remind).setVisible(false);
        }
        return true;
    }

    //ActionBar菜单点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     //返回箭头
                if (intent.getStringExtra("from").equals("note") && isEdit) {//便笺的编辑状态并未修改Navigation图标，所以需要在此处保存
                    ContentValues values = new ContentValues();
                    values.put("content", etContent.getText().toString());
                    database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
                }
                finish();
                break;
            case R.id.action_delete:    //删除
                if (intent.getStringExtra("from").equals("diary")) {
                     dialogDiary = new AlertDialog.Builder(this)
                            .setTitle("删除")
                            .setMessage("确定删除该日记吗？")
                            .setPositiveButton("确定", this)
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                     dialogNote = new AlertDialog.Builder(this)
                            .setTitle("删除")
                            .setMessage("确定删除该便笺吗？")
                            .setPositiveButton("确定", this)
                            .setNegativeButton("取消", null)
                            .show();
                }
                break;
            case R.id.action_remind:
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(
                        this,
                        this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //其他View点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_edit:
                if (intent.getStringExtra("from").equals("diary")) {    //日记编辑状态需要改变Navigation图标
                    toolbar.setNavigationIcon(R.mipmap.ic_done_black_24dp);
                    toolbar.setNavigationOnClickListener(this);
                }

                tvContent.setVisibility(View.GONE);
                etContent.setText(tvContent.getText());
                etContent.setVisibility(View.VISIBLE);
                etContent.requestFocus();
                fabEdit.hide();
                SoftInput.show(etContent);  //显示软键盘

                isEdit = true;
                break;
            default:       //保存点击事件
                if (isEdit) {
                    ContentValues values = new ContentValues();
                    values.put("content", etContent.getText().toString());
                    if (intent.getStringExtra("from").equals("diary")) {
                        database.update("diary", values, "id = ?", new String[]{intent.getStringExtra("id")});
                    } else {
                        database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
                    }

                    toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_black_24dp);
                    toolbar.setNavigationOnClickListener(this);

                    etContent.setVisibility(View.GONE);
                    tvContent.setText(etContent.getText().toString());
                    tvContent.setVisibility(View.VISIBLE);
                    SoftInput.hide(etContent);
                    fabEdit.show();

                    isEdit = false;
                } else {            //未处于编辑状态需要销毁当前Activity
                    finish();
                }
                break;
        }
    }

    //Dialog点击事件
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (dialog.equals(dialogDiary)) {   //删除日记
            database.delete("diary", "id = ?", new String[]{intent.getStringExtra("id")});
        } else {                            //删除便笺
            database.delete("note", "id = ?", new String[]{intent.getStringExtra("id")});
        }
        finish();
    }

    //便笺提醒选择器设置监听
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tvRemind.setText(getString(R.string.note_remind, hourOfDay, minute));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, 2016);
        calendar.set(Calendar.MONTH, 3);
        calendar.set(Calendar.DAY_OF_MONTH, 3);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        Intent intentReceiver = new Intent(this, RemindReceiver.class);
        intentReceiver.putExtra("from", "note");
        intentReceiver.putExtra("id", intent.getStringExtra("id"));
        intentReceiver.putExtra("content", intent.getStringExtra("content"));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intentReceiver, 0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    //NestedScrollView滚动事件
    @Override
    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (scrollY - oldScrollY > 0) {
            fabEdit.hide();
        } else {
            fabEdit.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (isEdit) {
            ContentValues values = new ContentValues();
            values.put("content", etContent.getText().toString());
            if (intent.getStringExtra("from").equals("note")) {
                database.update("note", values, "id = ?", new String[]{intent.getStringExtra("id")});
            }
        }
        super.onBackPressed();
    }
}
