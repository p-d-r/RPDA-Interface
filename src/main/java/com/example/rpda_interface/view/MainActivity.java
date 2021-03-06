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
import com.example.rpda_interface.model.action.ActionKind;
import com.example.rpda_interface.repository.RSABaseRepository;
import com.example.rpda_interface.viewmodel.RPDAViewModel;


public class MainActivity extends Activity implements PopupMenu.OnMenuItemClickListener
{

    LinearLayout containerLayout;
    EditText linkStateText;
    RPDAViewModel rpdaViewModel;
    AutomatonCanvas automatonCanvas;
    HorizontalScrollView horizontalScroller;
    ScrollView verticalScroller;
    RSABaseRepository rsaBaseRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rsaBaseRepo = new RSABaseRepository();
        Thread concurrentActionListener = new Thread(rsaBaseRepo);
        concurrentActionListener.start();

        containerLayout = findViewById(R.id.containerLayout);
        linkStateText = findViewById(R.id.linkStateInput);
        rpdaViewModel = new RPDAViewModel(this);
        automatonCanvas = new AutomatonCanvas(this, rpdaViewModel);

        //HorizontalScrollView is customized to allow 2d scrolling
        //and to allow child-controls to access relevant touch- and gesture-events
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

        //ScrollView is customized to allow 2d scrolling
        //and to allow child-controls to access relevant touch- and gesture-events
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



    //TODO: create state when push is activated
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
        try {
            switch (item.getItemId()) {
                case R.id.push_pose:
                    rsaBaseRepo.sendActionInfo(ActionKind.PUSH_POSE);
                    return true;
                case R.id.push_subtask:
                    rsaBaseRepo.sendActionInfo(ActionKind.PUSH_SUBTASK);
                    return true;
                case R.id.push_object_id:
                    rsaBaseRepo.sendActionInfo(ActionKind.PUSH_OBJ_ID);
                    return true;
                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}