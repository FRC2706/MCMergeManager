package ca.team2706.scouting.mcmergemanager.powerup2018.gui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.AutoScoutingObject;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.TeleopScouting;

public class AutoScouting extends AppCompatActivity {

    private AutoScoutingObject autoScoutingObject2018 = new AutoScoutingObject();
    public int pointsScored;

    SeekBar simpleSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.powerup2018_activity_auto_scouting);
        // initiate  views

        }



    public void toTeleop(View view) {


        final CheckBox checkBox = (CheckBox) findViewById(R.id.baselineCheckbox);
        if (checkBox.isChecked()) {
            autoScoutingObject2018.setCrossedAutoLine(true);
        }
        else {
            autoScoutingObject2018.setCrossedAutoLine(false);
        }

        final CheckBox noAutocheckbox = (CheckBox) findViewById(R.id.noAutoCheckbox);
        if (noAutocheckbox.isChecked()) {
            autoScoutingObject2018.setNoAuto(true);
        }
        else {
            autoScoutingObject2018.setNoAuto(false);
        }

        final CheckBox malfunctioncheckbox = (CheckBox) findViewById(R.id.autoMalfunctionCheckbox);
        if (malfunctioncheckbox.isChecked()) {
            autoScoutingObject2018.setAutoMalfunction(true);
        }
        else {
            autoScoutingObject2018.setAutoMalfunction(false);
        }

        final CheckBox startedCheckbox = (CheckBox) findViewById(R.id.autoStartedCubeCheckbox);
        final CheckBox groundCheckbox = (CheckBox) findViewById(R.id.autoGroundCheckbox);

        if (startedCheckbox.isChecked() && !groundCheckbox.isChecked()) {
            autoScoutingObject2018.setPickupType(AutoScoutingObject.pickupType.STARTED_CUBE);

        } else if (groundCheckbox.isChecked() && !startedCheckbox.isChecked()) {
            autoScoutingObject2018.setPickupType(AutoScoutingObject.pickupType.GROUND);

        } else if (!startedCheckbox.isChecked() && !groundCheckbox.isChecked()) {
            autoScoutingObject2018.setPickupType(AutoScoutingObject.pickupType.NO_PICKUP);
        } else {
            autoScoutingObject2018.setPickupType(AutoScoutingObject.pickupType.NO_PICKUP);
            //place holder not sure what to do with multiple checkboxes clicked
        }

        final CheckBox switchCheckbox = (CheckBox) findViewById(R.id.autoSwitchCheckbox);
        final CheckBox scaleCheckbox = (CheckBox) findViewById(R.id.autoScaleCheckbox);
        final CheckBox exchangeCheckbox = (CheckBox) findViewById(R.id.autoExchangeCheckbox);
        final CheckBox failedCheckbox = (CheckBox) findViewById(R.id.autoFailedCheckbox);

        if (switchCheckbox.isChecked() && !scaleCheckbox.isChecked() && !exchangeCheckbox.isChecked() && !failedCheckbox.isChecked()) {
            autoScoutingObject2018.setDeliveryType(AutoScoutingObject.deliveryType.SWITCH);
        } else if (!switchCheckbox.isChecked() && scaleCheckbox.isChecked() && !exchangeCheckbox.isChecked() && !failedCheckbox.isChecked()) {
            autoScoutingObject2018.setDeliveryType(AutoScoutingObject.deliveryType.SCALE);
        } else if (!switchCheckbox.isChecked() && !scaleCheckbox.isChecked() && exchangeCheckbox.isChecked() && !failedCheckbox.isChecked()) {
            autoScoutingObject2018.setDeliveryType(AutoScoutingObject.deliveryType.EXCHANGE);
        } else if (!switchCheckbox.isChecked() && !scaleCheckbox.isChecked() && !exchangeCheckbox.isChecked() && failedCheckbox.isChecked()) {
            autoScoutingObject2018.setDeliveryType(AutoScoutingObject.deliveryType.FAILURE);
        } else if (!switchCheckbox.isChecked() && !scaleCheckbox.isChecked() && !exchangeCheckbox.isChecked() && !failedCheckbox.isChecked()) {
            autoScoutingObject2018.setDeliveryType(AutoScoutingObject.deliveryType.NO_DELIVERY);
        } else {
            autoScoutingObject2018.setDeliveryType(AutoScoutingObject.deliveryType.NO_DELIVERY);
        }


        Intent intent = new Intent(this, TeleopScouting.class);
        intent.putExtra("PreGameData", getIntent().getSerializableExtra("PreGameData"));
        intent.putExtra("AutoScoutingData", autoScoutingObject2018);
        startActivity(intent);
    }


}
