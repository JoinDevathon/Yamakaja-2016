package me.yamakaja.devathon2016.electricity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yamakaja on 05.11.16.
 */
public class ElectricityNetwork {

    private List<IElectricityProvider> providerList = new ArrayList<>();
    private List<IElectricityReceiver> receiverList = new ArrayList<>();

    public void join(ElectricityNetwork network) {
        providerList.addAll(network.getProviderList());
        receiverList.addAll(network.getReceiverList());
    }

    public void update() {
        int requested = 0;
        int demandingReceivers = 0;
        for (IElectricityReceiver receiver : receiverList) {
            requested += receiver.getDemand();
            if (receiver.getDemand() > 0)
                demandingReceivers++;
        }

        if (requested == 0)
            return;

        int available = 0;
        for (IElectricityProvider provider : providerList) {
            available += provider.pullEnergy(requested - available);
        }


        for (IElectricityReceiver receiver : receiverList) {
            if (receiver.getDemand() > 0) {
                available -= receiver.acceptEnergy(available);
            }
        }
    }

    public List<IElectricityProvider> getProviderList() {
        return providerList;
    }

    public List<IElectricityReceiver> getReceiverList() {
        return receiverList;
    }

    public void register(IElectricMachine machine) {
        if (machine instanceof IElectricityProvider) {
            providerList.add((IElectricityProvider) machine);
        }
        if (machine instanceof IElectricityReceiver) {
            receiverList.add((IElectricityReceiver) machine);
        }
    }

    public void unregister(IElectricMachine machine) {
        if (machine instanceof IElectricityProvider) {
            providerList.remove(machine);
        }
        if (machine instanceof IElectricityReceiver) {
            receiverList.remove(machine);
        }
    }
}
