package com.tangem.tangemtest._main.entry_point

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tangem.tangemtest.R
import com.tangem.tangemtest._main.MainViewModel
import com.tangem.tangemtest.commons.ActionType
import com.tangem.tangemtest.commons.NavigateOptions
import com.tangem.tangemtest.commons.getDefaultNavigationOptions
import kotlinx.android.synthetic.main.fg_entry_point.*

/**
 * Created by Anton Zhilenkov on 10.03.2020.
 */
class EntryPointFragment : Fragment() {

    private val navController: NavController by lazy { findNavController() }
    private lateinit var rvActions: RecyclerView

    private val mainActivityVM: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_entry_point, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        mainActivityVM.ldDescriptionSwitch.observe(viewLifecycleOwner, Observer {
            // do some thing
        })
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(activity)
        rvActions = rv_actions
        rvActions.layoutManager = layoutManager
        rvActions.addItemDecoration(DividerItemDecoration(activity, layoutManager.orientation))
        rvActions.adapter = RvActionsAdapter { type, position, data -> navigate(data.destinationId) }
    }

    override fun onResume() {
        super.onResume()
        val adapter: RvActionsAdapter = rvActions.adapter as? RvActionsAdapter ?: return

        adapter.setItemList(getNavigateOptions())
        adapter.notifyDataSetChanged()
    }

    private fun navigate(destinationId: Int) {
        navController.navigate(destinationId, null, getDefaultNavigationOptions())
    }

    private fun getNavigateOptions(): MutableList<NavigateOptions> {
        return mutableListOf(
                NavigateOptions(ActionType.Scan, R.id.action_nav_entry_point_to_nav_scan),
                NavigateOptions(ActionType.Sign, R.id.action_nav_entry_point_to_nav_sign),
                NavigateOptions(ActionType.Personalize, R.id.action_nav_entry_point_to_nav_personalize),
                NavigateOptions(ActionType.Depersonalize, R.id.action_nav_entry_point_to_nav_depersonalize),
                NavigateOptions(ActionType.CreateWallet, R.id.action_nav_entry_point_to_nav_wallet_create),
                NavigateOptions(ActionType.PurgeWallet, R.id.action_nav_entry_point_to_nav_wallet_purge),
                NavigateOptions(ActionType.ReadIssuerData, R.id.action_nav_entry_point_to_nav_issuer_read_data),
                NavigateOptions(ActionType.WriteIssuerData, R.id.action_nav_entry_point_to_nav_issuer_write_data),
                NavigateOptions(ActionType.ReadIssuerExData, R.id.action_nav_entry_point_to_nav_issuer_read_ex_data),
                NavigateOptions(ActionType.WriteIssuerExData, R.id.action_nav_entry_point_to_nav_issuer_write_ex_data),
                NavigateOptions(ActionType.ReadUserData, R.id.action_nav_entry_point_to_nav_user_read_data),
                NavigateOptions(ActionType.WriteUserData, R.id.action_nav_entry_point_to_nav_user_write_data)
        )
    }
}