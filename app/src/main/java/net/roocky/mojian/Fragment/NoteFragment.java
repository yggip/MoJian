package net.roocky.mojian.Fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.roocky.mojian.Activity.MainActivity;
import net.roocky.mojian.Adapter.NoteAdapter;
import net.roocky.mojian.Mojian;
import net.roocky.mojian.R;

/**
 * Created by roocky on 03/16.
 * 便箋Fragment
 */
public class NoteFragment extends BaseFragment {
    private NoteAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        RecyclerView rvNote = (RecyclerView)view.findViewById(R.id.rv_note);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adapter = new NoteAdapter(getContext(), null, null, null, -1);

        rvNote.setLayoutManager(manager);
        rvNote.setAdapter(adapter);
        //当由当前fragment切换至其他fragment时需要清空删除list
        deleteList.clear();
        positionList.clear();
        //根据滚动方向决定FAB是否显示
        super.addOnScrollListener(rvNote);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        return view;
    }

    public NoteAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        if (isDeleting) {
            adapter.setPositionList(positionList);      //设置Adapter的被选中的item的positionList
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        super.onItemLongClick(view, position);
        adapter.setPositionList(positionList);          //设置Adapter的被选中的item的positionList
    }

    public void flush(int action, int position) {
        super.flush(adapter, "note", action, position, -1);
    }

    @Override
    public void onResume() {
        super.onResume();
        flush(Mojian.FLUSH_ALL, -1);
        //不同Fragment菜单有区别
        getActivity().invalidateOptionsMenu();
        //当由当前fragment切换至其他fragment时需要清空删除list
        MainActivity.setBaseFragment(MainActivity.getNoteFragment());
        MainActivity.getBaseFragment().isDeleting = false;
        MainActivity.getBaseFragment().deleteList.clear();
        MainActivity.getBaseFragment().positionList.clear();
    }
}