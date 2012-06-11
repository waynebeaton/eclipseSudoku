/****************************************************************************
 * Copyright (c) 2007 Composent, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Composent, Inc. - initial API and implementation
 *****************************************************************************/
package org.eclipse.sudoku.ecf.presence.ui;

import java.util.Hashtable;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.datashare.IChannelContainerAdapter;
import org.eclipse.ecf.presence.roster.IRoster;
import org.eclipse.ecf.presence.ui.roster.AbstractRosterContributionItem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.sudoku.ecf.Activator;

public class SudokuShareRosterContributionItem extends
		AbstractRosterContributionItem {

	private static Hashtable sudokusharechannels = new Hashtable();

	public SudokuShareRosterContributionItem() {
	}

	public SudokuShareRosterContributionItem(String id) {
		super(id);
	}

	protected static SudokuShare getSudokuShare(ID containerID) {
		return (SudokuShare) sudokusharechannels.get(containerID);
	}

	protected static SudokuShare addSudokuShare(ID containerID, SudokuShare urlshare) {
		return (SudokuShare) sudokusharechannels.put(containerID, urlshare);
	}

	protected static SudokuShare removeSudokuShare(ID containerID) {
		return (SudokuShare) sudokusharechannels.remove(containerID);
	}

	private IAction[] createActionAdd(final ID containerID, final ID localID,
			final IChannelContainerAdapter channelAdapter) {
		IAction action = new Action() {
			public void run() {
				try {
					addSudokuShare(containerID, new SudokuShare(channelAdapter,localID));
				} catch (ECFException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		action
				.setText("Add Sudoku Game Listener");
		action.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/sudokuMain.gif"));
		return new IAction[] { action };
	}

	private IAction[] createActionRemove(final ID containerID,
			final SudokuShare sudokushare) {
		IAction action = new Action() {
			public void run() {
				sudokushare.dispose();
				removeSudokuShare(containerID);
			}
		};
		action
				.setText("Remove Sudoku Game Listener");
		action.setImageDescriptor(Activator.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/sudokuMain.gif"));
		return new IAction[] { action };
	}

	protected IAction[] makeActions() {
		final IRoster roster = getSelectedRoster();
		if (roster != null) {
			// Roster is selected
			IContainer c = getContainerForRoster(roster);
			if (c != null) {
				// Get existing sudokushare for this container (if it exists)
				SudokuShare sudokushare = getSudokuShare(c.getID());
				// If it does exist already, then create action to remove
				if (sudokushare != null)
					return createActionRemove(c.getID(), sudokushare);
				else {
					IChannelContainerAdapter channelAdapter = (IChannelContainerAdapter) c
							.getAdapter(IChannelContainerAdapter.class);
					return (channelAdapter == null) ? null : createActionAdd(c
							.getID(), roster.getUser().getID(), channelAdapter);
				}
			}
		}
		// Not for us
		return null;
	}

}
