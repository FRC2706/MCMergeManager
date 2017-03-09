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
import ca.team2706.scouting.mcmergemanager.steamworks2017.dataObjects.GearDelivevryEvent;
import static ca.team2706.scouting.mcmergemanager.steamworks2017.gui.TeleopScouting.GEAR_DELIVERY_EVENT_STRING;

/**
 * Created by JustinT on 2017-02-09.
 */

public class GearDeliveryFragment extends DialogFragment {

    private FragmentListener listener;
    private GearDelivevryEvent gearDelivevryEvent = new GearDelivevryEvent();
    public Bundle gearDeliveryData = new Bundle();
    public boolean gearDropped;

    public GearDeliveryFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static GearDeliveryFragment newInstance(String title, FragmentListener listener, boolean gearDropped) {
        GearDeliveryFragment frag = new GearDeliveryFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        frag.listener = listener;
        frag.gearDropped = gearDropped;
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gear_delivery, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final DialogFragment me = this;

        view.findViewById(R.id.gear_delivery_cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

        view.findViewById(R.id.boiler_side_gear_delivery_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gearDelivevryEvent.lift = GearDelivevryEvent.Lift.BOILER_SIDE;
                        gearDelivevryEvent.deliveryStatus = GearDelivevryEvent.GearDeliveryStatus.DELIVERED;

                        gearDeliveryData.putSerializable(GEAR_DELIVERY_EVENT_STRING, gearDelivevryEvent);
                        listener.editNameDialogComplete(me, gearDeliveryData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

        view.findViewById(R.id.middle_gear_delivery_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gearDelivevryEvent.lift = GearDelivevryEvent.Lift.CENTRE;
                        gearDelivevryEvent.deliveryStatus = GearDelivevryEvent.GearDeliveryStatus.DELIVERED;

                        gearDeliveryData.putSerializable(GEAR_DELIVERY_EVENT_STRING, gearDelivevryEvent);
                        // this is the gearDeliveryData we want to hand back to the activity
                        listener.editNameDialogComplete(me, gearDeliveryData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

        view.findViewById(R.id.feeder_side_gear_delivery_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gearDelivevryEvent.lift = GearDelivevryEvent.Lift.FEEDER_SIDE;
                        gearDelivevryEvent.deliveryStatus = GearDelivevryEvent.GearDeliveryStatus.DELIVERED;

                        gearDeliveryData.putSerializable(GEAR_DELIVERY_EVENT_STRING, gearDelivevryEvent);
                        listener.editNameDialogComplete(me, gearDeliveryData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        listener.editNameDialogCancel(this);
    }

}
