package settings.connection

import customTestCase.PluginTestCase
import eu.ibagroup.formainframe.config.configCrudable
import eu.ibagroup.formainframe.config.connect.ConnectionConfig
import eu.ibagroup.formainframe.config.connect.Credentials
import eu.ibagroup.formainframe.config.connect.ui.ConnectionConfigurable
import eu.ibagroup.formainframe.config.connect.ui.ConnectionDialogState
import eu.ibagroup.formainframe.config.connect.ui.ConnectionsTableModel
import eu.ibagroup.formainframe.config.sandboxCrudable
import eu.ibagroup.formainframe.utils.crudable.getAll
import junit.framework.TestCase
import kotlin.streams.toList

/**
 * Testnig the connection manager on API level.
 */
class ConnectionManagerTest: PluginTestCase() {

    private lateinit var conTab: ConnectionsTableModel
    private val conConfig = ConnectionConfigurable()

    private val conStateA = ConnectionDialogState(connectionName = "a", connectionUrl = "https://a.com",
        username = "a", password = "a")
    private val conStateB = ConnectionDialogState(connectionName = "b", connectionUrl = "https://b.com",
        username = "b", password = "b")

    override fun setUp() {
        super.setUp()
        conTab = ConnectionsTableModel(sandboxCrudable)
    }

    /**
     * There is a need to eliminate all rows in the ConnectionTableModel after each test
     */
    override fun tearDown() {
        for (item in conTab.fetch(sandboxCrudable)) {
            conTab.onDelete(sandboxCrudable,item)
        }
        super.tearDown()
    }

    /**
     * The function checking whether the sandboxCrudable and configCrudable are modified accrodingly
     */
    fun assertCrudable(connectionDialogStateList: List<ConnectionDialogState>) {
        var conConfigSet = emptySet<ConnectionConfig>()
        var creSet = emptyList<Credentials>()
        for (connectionDialogState in connectionDialogStateList) {
            conConfigSet += connectionDialogState.connectionConfig
            creSet += connectionDialogState.credentials
        }
        TestCase.assertEquals(conConfigSet.toList(),sandboxCrudable.getAll<ConnectionConfig>().toList())
        TestCase.assertEquals(conConfigSet.toList(),configCrudable.getAll<ConnectionConfig>().toList())
        TestCase.assertEquals(creSet,sandboxCrudable.getAll<Credentials>().toList())
    }

    /**
     * testing the onAdd method of ConnectionTableModel on API level,
     * meaning checking whether the sandboxCrudable and configCrudable are modified accrodingly
     */
    fun testOnAdd() {
        conTab.onAdd(sandboxCrudable, conStateA)
        conTab.onAdd(sandboxCrudable, conStateB)
        conConfig.apply()
        assertCrudable(listOf(conStateA, conStateB))
        conTab.onDelete(sandboxCrudable,conStateA)
        conTab.onDelete(sandboxCrudable,conStateB)
        conConfig.apply()
        assertCrudable(listOf())
    }

    /**
     * Tests what happens to the sandboxCrudable if two connections with the same name are added.
     */
    fun testOnAddExistingName() {
        val connectionDialogState = ConnectionDialogState(connectionName = conStateA.connectionName)
        conTab.onAdd(sandboxCrudable, conStateA)
        conTab.onAdd(sandboxCrudable, connectionDialogState)
        conConfig.apply()
        assertCrudable(listOf(conStateA))
        conTab.onDelete(sandboxCrudable,conStateA)
        assertCrudable(listOf())
    }

    /**
     * Tests what happens to the sandboxCrudable if two connections with the same url are added.
     */
    fun testOnAddExistingUrl() {
        val connectionDialogState = ConnectionDialogState(connectionUrl = conStateA.connectionUrl)
        conTab.onAdd(sandboxCrudable, conStateA)
        conTab.onAdd(sandboxCrudable, connectionDialogState)
        conConfig.apply()
        assertCrudable(listOf(conStateA, connectionDialogState))
        conTab.onDelete(sandboxCrudable,conStateA)
        conTab.onDelete(sandboxCrudable,connectionDialogState)
        assertCrudable(listOf())
    }


}