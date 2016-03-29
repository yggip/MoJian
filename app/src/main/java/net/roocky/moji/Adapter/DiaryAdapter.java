package net.roocky.moji.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.roocky.moji.Database.DatabaseHelper;
import net.roocky.moji.Model.Diary;
import net.roocky.moji.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by roocky on 03/17.
 * 日记RecyclerView适配器
 */
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {
    private DatabaseHelper databaseHelper;
    private List<Diary> diaryList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public DiaryAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context, "Moji.db", null, 1);
        listRefresh();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //数据在数据库中是倒序排列的，即日期最早的数据在最前面，所以此处的index需要减去position
        holder.tvDate.setText(diaryList.get(diaryList.size() - position - 1).getDate());
        holder.tvContent.setText(diaryList.get(diaryList.size() - position - 1).getContent());
        holder.cvDiaryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, holder.getLayoutPosition());
            }
        });
        //将该item在数据库中的id存入CardView中
        holder.cvDiaryItem.setTag(diaryList.get(diaryList.size() - position - 1).getId());
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;
        private TextView tvContent;
        private CardView cvDiaryItem;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView)itemView.findViewById(R.id.tv_date);
            tvContent = (TextView)itemView.findViewById(R.id.tv_content);
            cvDiaryItem = (CardView)itemView.findViewById(R.id.cv_diary_item);
        }
    }

    //刷新listDate&listContent
    public void listRefresh() {
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        Cursor cursor = database.query("diary", null, null, null, null, null, null);
        diaryList.clear();
        if (cursor.moveToFirst()) {
            do {
                diaryList.add(new Diary(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("date")),
                        cursor.getString(cursor.getColumnIndex("content"))));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
