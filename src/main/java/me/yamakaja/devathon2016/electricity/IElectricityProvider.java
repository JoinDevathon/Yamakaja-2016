package me.yamakaja.devathon2016.electricity;

/**
 * Created by Yamakaja on 05.11.16.
 */
public interface IElectricityProvider extends IElectricMachine {

    /**
     * @param maxAmount The maximum amount of electricity to pull
     * @return The amount of electricity actually pulled
     */
    int pullEnergy(int maxAmount);

    int getMaxBufferSize();

}
