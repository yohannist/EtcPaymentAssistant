package com.apptech.yohannes.paymentassistant.fragments.mobile;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.apptech.yohannes.paymentassistant.R;
import com.apptech.yohannes.paymentassistant.activities.CameraActivity;
import com.apptech.yohannes.paymentassistant.core.BalanceCheckTask;
import com.apptech.yohannes.paymentassistant.core.BalanceFillTask;
import com.apptech.yohannes.paymentassistant.core.ITask;
import com.apptech.yohannes.paymentassistant.helpers.Util;

public class MobileFragment extends Fragment  {

    //View elements
    private Button btnCheck, btnFill, btnOCR;
    private EditText etCardNumber;
    private ContactListFragment contactListFragment;
    private OnFragmentInteractionListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_mobile, container, false);

        btnCheck = (Button)view.findViewById(R.id.btnCheck);
        btnFill = (Button)view.findViewById(R.id.btnFillBalance);
        btnOCR = (Button)view.findViewById(R.id.btnOcr);
        etCardNumber = (EditText)view.findViewById(R.id.etPhoneNumber);

        EventHandler eventHandler = new EventHandler();
        btnCheck.setOnClickListener(eventHandler);
        btnFill.setOnClickListener(eventHandler);
        btnOCR.setOnClickListener(eventHandler);

        //To make sure the keyboard shows up everytime user taps the card number EditTextView
        etCardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etCardNumber, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        mListener.ShowContactListFragment();
        return view;
    }

    public interface OnFragmentInteractionListener {
        public void ShowContactListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == -1)
            if(requestCode == 9)
                etCardNumber.setText(data.getStringExtra("detectedText"));
    }

    private class EventHandler implements View.OnClickListener, View.OnKeyListener
    {
        @Override
        public void onClick(View view) {
            if(view == btnCheck)
            {
                ITask checkTask = new BalanceCheckTask(getActivity());
                checkTask.Execute();
            }
            else if(view == btnFill)
            {
                if(!Util.IsValidCardNumber(etCardNumber.getText().toString()))
                {
                    Toast.makeText(getActivity(), "Please put in a valid card number", Toast.LENGTH_LONG).show();
                    return;
                }

                ITask fillTask = new BalanceFillTask(getActivity(), etCardNumber.getText().toString());
                fillTask.Execute();
            }
            else if(view == btnOCR)
            {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivityForResult(intent, 9);
            }
        }

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if(view == etCardNumber)
            {
                String numberString = etCardNumber.getText().toString();
                if(numberString.length() != 0 && numberString.length() % 3 == 0)
                {
                    etCardNumber.setText(etCardNumber.getText() + ", ");
                    etCardNumber.setSelection(etCardNumber.getText().length() - 1);
                }
            }
            return false;
        }
    }

}