package ca.team2706.scouting.mcmergemanager.powerup2018;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewGroupCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.team2706.scouting.mcmergemanager.powerup2018.dataObjects.CubeDroppedEvent;
import ca.team2706.scouting.mcmergemanager.steamworks2017.gui.Popups.FragmentListener;
import ca.team2706.scouting.mcmergemanager.R;

/**
 * Created by LaraLim on 2018-01-31.
 */

public class DroppedCubeFragment extends DialogFragment {

    public DroppedCubeFragment(){}

    private FragmentListener listener;
    public  Bundle cubeDropData = new Bundle();
    private CubeDroppedEvent cubeDroppedEvent = new CubeDroppedEvent();

    public static DroppedCubeFragment newInstance(String title, FragmentListener listener){

        DroppedCubeFragment droppedCubeFragment = new DroppedCubeFragment();
        Bundle argument = new Bundle();

        argument.putString("title", title);

        droppedCubeFragment.setArguments(argument);

        droppedCubeFragment.listener = listener;

        return droppedCubeFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dropped_cube, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final DialogFragment me = this;

        view.findViewById(R.id.cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

        view.findViewById(R.id.fumble_pickup_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cubeDroppedEvent.dropType = CubeDroppedEvent.DropType.FUMBLE;
                        cubeDropData.putSerializable("dropped_cube", cubeDroppedEvent);
                        listener.editNameDialogComplete(me, cubeDropData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

        view.findViewById(R.id.easy_pickup_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cubeDroppedEvent.dropType = CubeDroppedEvent.DropType.EASY_PICKUP;
                        cubeDropData.putSerializable("dropped_cube", cubeDroppedEvent);
                        listener.editNameDialogComplete(me, cubeDropData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );

        view.findViewById(R.id.left_it_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cubeDroppedEvent.dropType = CubeDroppedEvent.DropType.LEFT_IT;
                        cubeDropData.putSerializable("dropped_cube", cubeDroppedEvent);
                        listener.editNameDialogComplete(me, cubeDropData);

                        Log.i(getClass().getName(), "quit");
                        listener.editNameDialogCancel(me);
                    }
                }
        );





    }

}
