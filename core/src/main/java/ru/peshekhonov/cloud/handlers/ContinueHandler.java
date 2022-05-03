package ru.peshekhonov.cloud.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ru.peshekhonov.cloud.Configuration;
import ru.peshekhonov.cloud.StatusType;
import ru.peshekhonov.cloud.messages.Message;
import ru.peshekhonov.cloud.messages.StatusData;
import ru.peshekhonov.cloud.messages.SubsequentData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class ContinueHandler extends SimpleChannelInboundHandler<Message> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        if (msg instanceof SubsequentData data) {
            Path path = data.getPath();
            SeekableByteChannel writeChannel = ctx.pipeline().get(StartHandler.class).getMap().get(path);
            String filename = path.getFileName().toString();
            log.info("Continue frame of the file \"{}\" is received", filename);
            try {
                ByteBuffer buffer = ByteBuffer.allocate(Configuration.BUFFER_SIZE);
                buffer.put(data.getData());
                buffer.flip();
                writeChannel.write(buffer);
                buffer.clear();
                if (data.isEndOfFile()) {
                    writeChannel.close();
                    ctx.writeAndFlush(new StatusData(path, StatusType.OK));
                    log.info("The file \"{}\" is successfully saved", filename);
                }
            } catch (IOException e) {
                String str = "I/O error";
                ctx.writeAndFlush(new StatusData(path, StatusType.ERROR1, str));
                log.error("File \"{}\": {}", filename, str);
                try {
                    writeChannel.close();
                    Files.deleteIfExists(path);
                } catch (IOException ex) {
                    log.error("File \"" + filename + "\"", ex);
                }
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
