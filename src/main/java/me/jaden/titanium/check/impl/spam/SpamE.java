package me.jaden.titanium.check.impl.spam;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.netty.buffer.ByteBufHelper;
import com.github.retrooper.packetevents.netty.buffer.UnpooledByteBufAllocationHelper;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import me.jaden.titanium.check.PacketCheck;
import me.jaden.titanium.data.PlayerData;

public class SpamE implements PacketCheck {

    //Fixes console spammer with register/unregister payloads

    @Override
    public void handle(PacketReceiveEvent event, PlayerData playerData) {
        if (event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
            if (wrapper.getChannelName().equals("REGISTER") || wrapper.getChannelName().equals("UNREGISTER")) {
                Object buffer = null;
                try {
                    buffer = UnpooledByteBufAllocationHelper.buffer();
                    ByteBufHelper.writeBytes(buffer, wrapper.getData());
                    PacketWrapper<?> universalWrapper = PacketWrapper.createUniversalPacketWrapper(buffer);
                    String payload = universalWrapper.readString();
                    if (payload.split("\u0000").length > 124) {
                        flag(event);
                    }
                } finally {
                    ByteBufHelper.release(buffer);
                }
            }
        }
    }
}
