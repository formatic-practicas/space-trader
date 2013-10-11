// $codepro.audit.disable variableShouldBeFinal, com.instantiations.assist.eclipse.analysis.unusedReturnValue
/**
 * Contains PlanetView and dialog fragment
 */
package com.cs2340.spacetrader; // $codepro.audit.disable packageNamingConvention

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays menu for planet actions
 * 
 * @author Andrew
 * @version 1.0
 */
public class PlanetView extends FragmentActivity {
	/** the current activity */
	private Context context = this;

	/** planet's contract */
	public Contract contract;

	/**
	 * set's up the activity upon creation
	 * 
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_planet);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		/*
		 * if
		 * (GameSetup.theMap.getPlanet(GameSetup.thePlayer.getship().getPlanetName
		 * ()).getNTechLevel() > 2){
		 * pv.setBackgroundDrawable(rs.getDrawable((R.drawable.hightechback)));
		 * } else{
		 * pv.setBackgroundDrawable(rs.getDrawable((R.drawable.lowtechback))); }
		 */

		TextView name = (TextView) findViewById(R.id.planet_current_planet);
		name.setText(GameSetup.thePlayer.getship().getPlanetName());

		Button contractButton = (Button) findViewById(R.id.planet_button_contract);
		if (GameSetup.thePlayer.hasContract) {
			contractButton.setEnabled(false);
		} else {
			contractButton.setEnabled(true);
		}
	}

	/**
	 * sets up menu layout
	 * 
	 * @param menu
	 * @return success or not
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_options, menu);
		return true;
	}

	/**
	 * populates menu layout
	 * 
	 * @param item
	 * @return success or not
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.save_game:
			item.setEnabled(false);
			item.setTitle("Saving");
			SaveState state = new SaveState(GameSetup.thePlayer,
					GameSetup.theMap);
			if (MemoryService.saveGame(state, this)) {
				Toast.makeText(this, "Game saved successfully",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "Failed to save game", Toast.LENGTH_SHORT)
						.show();
			}
			item.setTitle("Save");
			item.setEnabled(true);
			return true;
		case R.id.quit_game:
			Intent intent = new Intent(this, Launcher.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Overrides toString because audit complains
	 * 
	 * @return a random string
	 */
	@Override
	public String toString() {
		return "blah";
	}

	/**
	 * Continue to TradeView from PlanetView
	 * 
	 * @param view
	 *            PlanetView
	 */
	public void startTradeView(View view) {
		Intent intent = new Intent(PlanetView.this, TradeView.class);
		startActivity(intent);
	}

	/**
	 * GUI returns to Space from PlanetView
	 * 
	 * @param view
	 *            PlanetView
	 */
	public void startSpaceView(View view) {
		Intent intent = new Intent(PlanetView.this, Space.class);
		startActivity(intent);
	}

	/**
	 * Continue to UpgradeShipView
	 * 
	 * @param view
	 */
	public void upgradeShip(View view) {
		Intent intent = new Intent(PlanetView.this, UpgradeShipView.class);
		startActivity(intent);
	}

	/**
	 * Randomly generates the contract
	 */
	public void createContract() {
		contract = GameSetup.theMap.getPlanet(
				GameSetup.thePlayer.getship().getPlanetName()).getContract();
		contract.generateContract(); // add param "toGo", "fightPirate",
										// "bringGood" to test specific
										// contracts
	}

	/**
	 * Displays a dialog to accept or reject a contract
	 * 
	 * @param view
	 */
	public void lookForContract(View view) {
		createContract();

		FragmentManager fm = getSupportFragmentManager();
		ContractFragment editNameDialog = new ContractFragment();
		editNameDialog.dialog = contract.getString();
		editNameDialog.show(fm, "fragment_contract");
	}

	/**
	 * Refuels the ship
	 * 
	 * @param view
	 */
	public void refuel(View view) {
		GameSetup.thePlayer.getship().refuel();
		Toast.makeText(this, R.string.ship_refueled, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Class for creating the dialog box to accept/reject a contract
	 * 
	 * @author David
	 * 
	 */
	@SuppressLint("ValidFragment")
	private class ContractFragment extends DialogFragment { // $codepro.audit.disable
															// com.instantiations.assist.eclipse.analysis.audit.rule.effectivejava.favorStaticMemberClassesOverNonStatic
		/** dialog string to be set later */
		public String dialog = "";

		/**
		 * Creates the dialog activity
		 * 
		 * @param savedInstanceState
		 * @return Dialog
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setTitle(R.string.contract_title);
			builder.setMessage(dialog);
			builder.setPositiveButton(R.string.contract_accept,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							GameSetup.thePlayer.hasContract = true;
							GameSetup.thePlayer.setContract(contract);
							Button contractButton = (Button) findViewById(R.id.planet_button_contract);
							contractButton.setEnabled(false);
							Toast.makeText(context,
									"You've accepted the contract",
									Toast.LENGTH_LONG).show();
						}
					}).setNegativeButton(R.string.contract_decline,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// User cancelled the dialog
							Log.i("Canceled", "Nothing happens!");
						}
					});
			// Create the AlertDialog object and return it
			return builder.create();
		}
	}
}
