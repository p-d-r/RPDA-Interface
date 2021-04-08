package com.example.rpda_interface.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.rpda_interface.R;
import com.example.rpda_interface.viewmodel.RPDAViewModel;


public class MainActivity extends Activity implements PopupMenu.OnMenuItemClickListener
{

    LinearLayout containerLayout;
    EditText linkStateText;
    RPDAViewModel rpdaViewModel;
    AutomatonCanvas automatonCanvas;
    HorizontalScrollView horizontalScroller;
    ScrollView verticalScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        containerLayout = findViewById(R.id.containerLayout);
        linkStateText = findViewById(R.id.linkStateInput);
        rpdaViewModel = new RPDAViewModel(this);
        automatonCanvas = new AutomatonCanvas(this, rpdaViewModel);

        horizontalScroller = new HorizontalScrollView(this) {
            View content;

            @Override
            public void addView(View view){
                super.addView(view);
                content = view;
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                content.onTouchEvent(event);
                return super.onTouchEvent(event);
            }
        };

        verticalScroller = new ScrollView(this) {
            HorizontalScrollView horizontalScroller;

            @Override
            public void addView(View view){
                super.addView(view);
                if (view instanceof HorizontalScrollView)
                        horizontalScroller = (HorizontalScrollView) view;
            }

            @Override
            public boolean onTouchEvent(MotionEvent event) {
                super.onTouchEvent(event);
                horizontalScroller.dispatchTouchEvent(event);
                return true;
            }

            @Override public boolean onInterceptTouchEvent(MotionEvent event)
            {
                super.onInterceptTouchEvent(event);
                horizontalScroller.onInterceptTouchEvent(event);
                return true;
            }
        };

        horizontalScroller.addView(automatonCanvas);
        verticalScroller.addView(horizontalScroller);
        containerLayout.addView(verticalScroller);

    }

    public void createState(View view) {

        rpdaViewModel.handleStateAction(this);
        automatonCanvas.invalidate();
    }


    public void linkState(View view) {

        int id;
        String text = linkStateText.getText().toString();
        CharSequence message;

        if (!text.equals("")) {
            id = Integer.parseInt(text);
            if (rpdaViewModel.handleLinkAction(id))
                message = "link inserted!";  //TODO: refactor to resource
            else
                message = "target state id not found!";
        } else {
            message = "given state-identifier is not a number!";
        }

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, message, duration);
        toast.show();

        automatonCanvas.invalidate();
    }

    public void undoAction(View view) {
        rpdaViewModel.undo();
        automatonCanvas.invalidate();
    }

    public void redoAction(View view) {
        rpdaViewModel.redo();
        automatonCanvas.invalidate();
    }

    public void pushAction(View view) {

    }

    public void showPushActionMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(MainActivity.this);
        popup.inflate(R.menu.push_action_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.push_pose:
                ;
                return true;
            case R.id.push_subtask:
                ;
                return true;
            case R.id.push_object_id:
                ;
                return true;
            default:
                return false;
        }
    }

}