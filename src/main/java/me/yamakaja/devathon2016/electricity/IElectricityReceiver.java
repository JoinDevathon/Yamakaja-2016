package me.yamakaja.devathon2016.electricity;

/**
 * Created by Yamakaja on 05.11.16.
 */
public interface IElectricityReceiver extends IElectricMachine {

    int getDemand();
    int acceptEnergy(int amount);

}
