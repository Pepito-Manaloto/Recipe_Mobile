package com.aaron.recipe.listener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;

import com.aaron.recipe.fragment.AboutFragment;
import com.aaron.recipe.function.Action;
import com.aaron.recipe.model.LogsManager;
import com.aaron.recipe.model.RecipeManager;

import java.lang.ref.WeakReference;

/**
 * Handles the deleting of all Recipe upon long click/press.
 */
public class DeleteLongClickListener implements View.OnLongClickListener
{
    private static final String YES = "Yes";
    private static final String No = "No";

    private RecipeManager recipeManager;
    private WeakReference<AboutFragment> fragmentRef;
    private Action postDeleteAction;

    /**
     * Default Constructor.
     *
     * @param fragmentRef the fragment where the delete event is
     * @param recipeManager the model that handles the deletion of recipes
     * @param postDeleteAction the function that executes after the delete, which is to navigate to the next activity
     */
    public DeleteLongClickListener(final AboutFragment fragmentRef, RecipeManager recipeManager, Action postDeleteAction)
    {
        this.fragmentRef = new WeakReference<>(fragmentRef);
        this.recipeManager = recipeManager;
        this.postDeleteAction = postDeleteAction;
    }

    /**
     * Pops-up a prompt dialog with 'yes' or 'no' button. Selecting 'yes' will delete all vocabularies from disk.
     */
    @Override
    public boolean onLongClick(View view)
    {
        promptUserOnDelete();
        return true;
    }

    /**
     * Pops-up a prompt dialog with 'yes' or 'no' button.
     * Selecting 'yes' will delete all recipes from disk.
     */
    private void promptUserOnDelete()
    {
        LogsManager.log(AboutFragment.CLASS_NAME, "promptUserOnDelete", "");

        final AboutFragment fragment = this.fragmentRef.get();

        if(fragment != null)
        {
            AlertDialog.Builder prompt = new AlertDialog.Builder(fragment.getActivity());
            prompt.setMessage("Delete recipes from disk?");

            prompt.setPositiveButton(YES, this::yesButtonAction);
            prompt.setNegativeButton(No, this::noButtonAction);

            prompt.create().show();
        }
    }

    private void yesButtonAction(DialogInterface dialog, int id)
    {
        logDialogAction(YES);

        recipeManager.deleteRecipeFromDisk();
        postDeleteAction.execute();
    }

    private void noButtonAction(DialogInterface dialog, int id)
    {
        logDialogAction(No);

        dialog.cancel();
    }

    private void logDialogAction(String action)
    {
        LogsManager.log(AboutFragment.CLASS_NAME, "promptUserOnDelete", action + "  selected.");
    }
}