package me.yamakaja.devathon2016.electricity;

/**
 * Created by Yamakaja on 05.11.16.
 */
public interface IElectricMachine {

    ElectricityNetwork getNetwork();
    int getBuffer();
    void setBuffer(int amount);

}
