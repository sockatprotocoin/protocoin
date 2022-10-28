package net.ddns.protocoin;

import net.ddns.protocoin.core.blockchain.Blockchain;
import net.ddns.protocoin.core.util.ArrayUtil;
import net.ddns.protocoin.core.util.Converter;
import net.ddns.protocoin.service.AppContext;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Main {
    private static final String block1 = "06090609000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000006354386E200696F483F43A35010001000000012A05F200180102D0ECF5C0DCF3797201379ED48F2DE37002FE823C0305";
    private static final String block2= "0609060900B575D1907A670B2BC9DE6008E29F0DC7761F9D40FCF0E22F9F5F9F55D6F68E0000000000000000000000000000000000000000000000000000000000000000635438D4200696F45F02AF1C0101E5CBC4C2F7299C9BE688BF97F622FAEC13AC701439C8C36F856D1131904F91E50000000079BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F8179879BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798661BA57FED0D115222E30FE7E9509325EE30E7E284D3641E6FB5E67368C2DB185ADA8EFC5DC43AF6BF474A41ED6237573DC4ED693D49102C42FFC8851050079901000000012A05F20018010276B0F3B2743504B9D3E1D04022905548CFEDC7A60305";

    public static void main(String[] args) throws IOException {
        var context = new AppContext();
        context.getBlockChainService().loadBlockChainToUTXOStorage(
                Blockchain.readFromInputStream(
                        new ByteArrayInputStream(
                                ArrayUtil.concat(
                                        Converter.hexStringToByteArray(block1),
                                        Converter.hexStringToByteArray(block2)
                                )
                        )
                )
        );
        var node = context.getNode();

        node.startListening(6969);
    }
}
