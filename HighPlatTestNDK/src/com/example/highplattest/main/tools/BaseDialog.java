package com.example.highplattest.main.tools;

import com.example.highplattest.R;
import com.example.highplattest.main.constant.GlobalVariable;
import com.example.highplattest.main.constant.NDK;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class BaseDialog extends Dialog  implements android.view.View.OnClickListener,OnItemClickListener,NDK{
    //	private static final String TAG = "AlertDialog";

	   /**
     * 自定义Dialog监听器
     */
    public interface OnDialogButtonClickListener {

        /**
         * 点击按钮事件的回调方法
         *
         * @param requestCode 传入的用于区分某种情况下的showDialog
         * @param isPositive
         */
        void onDialogButtonClick(View view, boolean isPositive);
        
    }
    

    private Context context;
    private String title;
    private String message;
    private String strPositive;
    private String strNegative;
    private Boolean showNegativeButton=true;
    private OnDialogButtonClickListener listener;
    private View dialogView;  
    private ListView listView;
    
    public View getDialogView() {
		return dialogView;
	}
	public void setDialogView(View dialogView) {
		this.dialogView = dialogView;
	}
	/**
	 *没有【取消】【确定】按钮
	 * @param context
	 * @param view
	 * @param title
	 */
	public BaseDialog(Context context, View view,String title) {
		super(context);
		this.context = context;
		this.title = title;
		this.dialogView=view;
	}
	/**
	 * 传入Listview 只有【取消】按钮
	 * @param context
	 * @param view
	 * @param title
	 * @param strNegative
	 * @param showNegativeButton
	 * @param listener
	 */
	public BaseDialog(Context context, ListView view,String title,String strNegative,OnDialogButtonClickListener listener) {
		super(context);
		this.context = context;
		this.title = title;
		this.strNegative = strNegative;
		this.listView = view;
		this.listener=listener;  
	}
	/**
	 * 传入view 只有【取消】按钮
	 * @param context
	 * @param view
	 * @param title
	 * @param strNegative
	 * @param showNegativeButton
	 * @param listener
	 */
	public BaseDialog(Context context, View view,String title,String strNegative,OnDialogButtonClickListener listener) {
		super(context);
		this.context = context;
		this.title = title;
		this.strNegative = strNegative;
		this.dialogView = view;
		this.listener=listener;  
	}
	/**
     * 传入view 有【确定】【取消】按钮
     */
	public BaseDialog(Context context,View view,String title,String strPositive, String strNegative,OnDialogButtonClickListener listener) {
		super(context);
		this.context = context;
		this.title = title;
		this.strPositive = strPositive;
		this.strNegative = strNegative;
		this.dialogView = view;
		this.listener=listener;  
	}
	
//	public BaseDialog(boolean flag,Context context,View view,String title,String strPositive, String strNegative,OnDialogButtonClickListener listener) {
//		super(context);
//		this.context = context;
//		this.title = title;
//		this.strPositive = strPositive;
//		this.strNegative = strNegative;
//		if (flag) {
//	RadioGroup rg_sec = (RadioGroup) view.findViewById(R.id.rg_ap_sec);
//	RadioGroup rg_ssid = (RadioGroup) view.findViewById(R.id.rg_ssid_broad);
//	RadioButton rb_nopass=view.findViewById(R.id.rb_sec_nopass);
//	RadioButton rb_wep=view.findViewById(R.id.rb_sec_wep);
//	rb_nopass.setVisibility(View.GONE);
//	rb_wep.setVisibility(View.GONE);
//		}
//		this.dialogView = view;
//		this.listener=listener;  
//	}
	/**
	 * 传入view 只有【确定】按钮
	 * @param context
	 * @param view
	 * @param title
	 * @param strPositive
	 * @param showNegativeButton
	 * @param listener
	 */
	public BaseDialog(Context context, View view,String title,String strPositive,Boolean showNegativeButton,OnDialogButtonClickListener listener) {
		super(context);
		this.context = context;
		this.title = title;
		this.strPositive = strPositive;
		//设置取消按钮为false
		this.showNegativeButton=showNegativeButton;
		this.dialogView = view;
		this.listener=listener;  
	}
	/**
	 * 传入message 有【确定】【取消】按钮
	 * @param context
	 * @param title
	 * @param message
	 * @param strPositive
	 * @param strNegative
	 * @param listener
	 */
    public BaseDialog(Context context, String title, String message,
                       String strPositive, String strNegative,OnDialogButtonClickListener listener) {
        super(context);
        this.context = context;
        this.title = title;
        this.message = message;
        this.strPositive = strPositive;
        this.strNegative = strNegative;
        this.listener=listener;  
    }
    /**
     * 传入message 只有【确定】按钮
     * @param context
     * @param title
     * @param message
     * @param strPositive
     * @param showNegativeButton
     * @param listener
     */
    public BaseDialog(Context context, String title, String message,
            String strPositive,Boolean showNegativeButton,OnDialogButtonClickListener listener) {
		super(context);
		this.context = context;
		this.title = title;
		this.message = message;
		this.strPositive = strPositive;
		this.showNegativeButton=showNegativeButton;
		this.listener=listener;  
    }
    /**
     * 传入viewId 有【确定】【取消】按钮
     * @param context
     * @param customeLayoutId
     * @param title
     * @param strPositive
     * @param strNegative
     * @param listener
     */
	public BaseDialog(Context context, int customeLayoutId, String title,String strPositive, String strNegative,OnDialogButtonClickListener listener) {
		super(context);
		this.context = context;
		this.title = title;
		this.strPositive = strPositive;
		this.strNegative = strNegative;
		//有界面
		this.dialogView=View.inflate(context,customeLayoutId,null); 
		this.listener=listener;  
	}
	
	public BaseDialog(boolean flag,Context context, int customeLayoutId, String title,String strPositive, String strNegative,OnDialogButtonClickListener listener) {
		super(context);
		this.context = context;
		this.title = title;
		this.strPositive = strPositive;
		this.strNegative = strNegative;
		//有界面
		this.dialogView=View.inflate(context,customeLayoutId,null); 
		if (flag) {
			RadioGroup rg_sec = (RadioGroup) dialogView.findViewById(R.id.rg_ap_sec);
			RadioGroup rg_ssid = (RadioGroup) dialogView.findViewById(R.id.rg_ssid_broad);
			RadioButton rb_nopass=dialogView.findViewById(R.id.rb_sec_nopass);
			RadioButton rb_wep=dialogView.findViewById(R.id.rb_sec_wep);
			rb_nopass.setVisibility(View.GONE);
			rb_wep.setVisibility(View.GONE);
		}
		this.listener=listener;  
	}
	private Button btnPositive;
    private Button btnNegative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_normal_layout, null);
        btnPositive = (Button)view.findViewById(R.id.positiveButton);
        btnNegative = (Button)view.findViewById(R.id.negativeButton);

        if(strPositive!=null){
        	  btnPositive.setVisibility(View.VISIBLE);
        	  btnPositive.setText(strPositive);
              btnPositive.setOnClickListener(this);
        }
        if(strNegative!=null && showNegativeButton){
        	 btnNegative.setVisibility(View.VISIBLE);
        	 btnNegative.setText(strNegative);
             btnNegative.setOnClickListener(this);
             LoggerUtil.e("enter enter enter");
        }
        if(message!=null){
        	((TextView)view.findViewById(R.id.message)).setText(message); 
        }
        if(dialogView!=null )
        {

        	((LinearLayout) view.findViewById(R.id.content)).removeAllViews();  
            ((LinearLayout) view.findViewById(R.id.content)).addView(dialogView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));  
        }
        //listView处理
        if(listView!=null)
        {

        	((LinearLayout) view.findViewById(R.id.content)).removeAllViews();  
            ((LinearLayout) view.findViewById(R.id.content)).addView(listView, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));  
            listView.setOnItemClickListener(this);
        }
        setTitle(title);
        //点击旁边不消失
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setDialogView(view);
        setContentView(view); 
    }
  
	@Override
    public void onClick(final View v) {
        if (v.getId() == R.id.positiveButton) {
            listener.onDialogButtonClick(dialogView,true);
            GlobalVariable.RETURN_VALUE = GlobalVariable.SUCC;
        } else if (v.getId() == R.id.negativeButton) {
            listener.onDialogButtonClick(dialogView, false);
            GlobalVariable.RETURN_VALUE = NDK_ERR_QUIT;
        }
        LoggerUtil.d("baseDialog onclick");
        dismiss();
    }
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	
		GlobalVariable.Position = position;
		GlobalVariable.RETURN_VALUE = GlobalVariable.SUCC;
		synchronized (context) {
			context.notify();
		}
		dismiss();
		
	}

}