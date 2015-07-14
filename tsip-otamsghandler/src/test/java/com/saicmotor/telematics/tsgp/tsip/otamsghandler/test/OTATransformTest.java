package com.saicmotor.telematics.tsgp.tsip.otamsghandler.test;

import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTADecoder;
import com.saicmotor.telematics.tsgp.otaadapter.asn.codec.OTAEncoder;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.DataEncodingType;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.MessageCounter;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.StartEndNumber;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_DispatcherBody;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_DispatcherHeader;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.dispatcher.AVN_OTARequest;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.drivingbehavior.RankingType;
import com.saicmotor.telematics.tsgp.otaadapter.avn.v1_1.entity.drivingbehavior.UserFuelEconomyRankingReq;
import com.saicmotor.telematics.tsgp.otaadapter.vp.v1_1.entity.dispatcher.VP_OTARequest;
import com.saicmotor.telematics.tsgp.tsip.otamsghandler.helper.OTATransform;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: cqzzl
 * Date: 13-9-5
 * Time: 下午2:02
 * To change this template use File | Settings | File Templates.
 */
public class OTATransformTest {
    @Test
    public void testTransform() throws Exception {
        AVN_OTARequest a = getAVNRequest();

        VP_OTARequest b = (VP_OTARequest) OTATransform.transform(AVN_OTARequest.class, VP_OTARequest.class, a,
                new OTATransform.Converter() {
                    @Override
                    public void convert(Object sourceRequest, Object targetRequest) {
                        AVN_OTARequest srcRequest = (AVN_OTARequest) sourceRequest;
                        VP_OTARequest tgtRequest = (VP_OTARequest) targetRequest;

                        com.saicmotor.telematics.tsgp.otaadapter.vp.v1_1.entity.drivingbehavior.UserFuelEconomyRankingReq target =
                                (com.saicmotor.telematics.tsgp.otaadapter.vp.v1_1.entity.drivingbehavior.UserFuelEconomyRankingReq) fastCopy(
                                        decode(UserFuelEconomyRankingReq.class, srcRequest.getApplicationData()), com.saicmotor.telematics.tsgp.otaadapter.vp.v1_1.entity.drivingbehavior.UserFuelEconomyRankingReq.class);

                        target.setNikname((new String(target.getNikname()) + " ...").getBytes());

                        tgtRequest.setApplicationData(encode(target));
                    }
                });

    }

    public static AVN_OTARequest getAVNRequest() {
        AVN_OTARequest request = new AVN_OTARequest();

        AVN_DispatcherHeader header = new AVN_DispatcherHeader();
        header.setDispatcherBodyEncoding(0);
        header.setProtocolVersion(17);
        header.setSecurityContext(0);


        AVN_DispatcherBody body = new AVN_DispatcherBody();
        body.setAckRequired(false);

        DataEncodingType encodingType = new DataEncodingType();
        encodingType.setValue(DataEncodingType.EnumType.perUnaligned);
        body.setApplicationDataEncoding(encodingType);
        body.setApplicationDataProtocolVersion(257);
        body.setApplicationID("000");
        body.setMessageID(1);
        MessageCounter counter = new MessageCounter();
        counter.setDownlinkCounter(1);
        counter.setUplinkCounter(0);
        body.setMessageCounter(counter);
        body.setTestFlag(1);
        body.setEventCreationTime(System.currentTimeMillis()/1000l);
        body.setVin("12345678901234567");
        body.setUid("00000000000000000000000000000000000000000000000000");
        body.setToken("12345678901234567890123456789012");

        request.setDispatcherBody(body);
        request.setDispatcherHeader(header);

        UserFuelEconomyRankingReq req = new UserFuelEconomyRankingReq();
        req.setNikname("昵称".getBytes());
        StartEndNumber st = new StartEndNumber();
        st.setStartNumber(1l);
        st.setEndNumber(100l);
        req.setStartEndNumber(st);
        RankingType type = new RankingType();
        type.setValue(RankingType.EnumType.year);
        req.setRankingType(type);

        byte[] bytes = encode(req);

        request.setApplicationData(bytes);
        body.setApplicationDataLength(0l + bytes.length);

        return request;
    }

    public static byte[] encode(Object obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OTAEncoder encoder = new OTAEncoder(outputStream);
        encoder.encode(obj);
        return outputStream.toByteArray();
    }

    public static Object decode(Class clz, byte[] applicationData) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(applicationData);
        OTADecoder decoder = new OTADecoder(inputStream);
        return decoder.decode(clz);
    }
}
