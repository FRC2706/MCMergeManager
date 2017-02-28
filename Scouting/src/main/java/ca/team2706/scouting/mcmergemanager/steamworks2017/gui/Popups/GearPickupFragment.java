package ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.team2706.scouting.mcmergemanager.R;
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.GearPickupEvent;

import static ca.team2706.scouting.mcmergemanager.steamworks2017.gui.TeleopScouting.teleopScoutingObject;
import static ca.team2706.scouting.mcmergemanager.steamworks2017.gui.TeleopScouting.GEAR_PICKUP_EVENT_STRING;
/**
 * Created by Merge on 2017-02-11.
 */

public class GearPickupFragment extends DialogFragment {

    private FragmentListener listener;
    private GearPickupEvent gearPickupEvent = new GearPickupEvent();
    public  Bundle gearPickupData = new Bundle();

    public GearPickupFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static GearPickupFragment newInstance(String title, FragmentListener listener, boolean gearHeld) {
        GearPickupFragment frag = new GearPickupFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        frag.listener = listener;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gear_pickup, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final DialogFragment me = this;

        view.findViewById(R.id.gear_pickup_cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

        view.findViewById(R.id.ground_gear_pickup_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gearPickupEvent.pickupType = GearPickupEvent.GearPickupType.GROUND;

                        gearPickupData.putSerializable(GEAR_PICKUP_EVENT_STRING, gearPickupEvent);
                        listener.editNameDialogComplete(me, gearPickupData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );
        view.findViewById(R.id.wall_gear_pickup_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gearPickupEvent.pickupType = GearPickupEvent.GearPickupType.WALL;

                        gearPickupData.putSerializable(GEAR_PICKUP_EVENT_STRING, gearPickupEvent);
                        listener.editNameDialogComplete(me, gearPickupData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

        view.findViewById(R.id.other_gear_pickup_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gearPickupEvent.pickupType = GearPickupEvent.GearPickupType.GROUND;

                        gearPickupData.putSerializable(GEAR_PICKUP_EVENT_STRING, gearPickupEvent);
                        listener.editNameDialogComplete(me, gearPickupData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        teleopScoutingObject.add(gearPickupEvent);
        listener.editNameDialogCancel(this);
    }
}
