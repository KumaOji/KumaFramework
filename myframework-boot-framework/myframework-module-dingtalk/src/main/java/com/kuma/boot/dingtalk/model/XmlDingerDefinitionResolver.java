/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.kuma.boot.common.utils.log.LogUtils
 *  org.springframework.core.io.Resource
 *  org.springframework.util.FileCopyUtils
 */
package com.kuma.boot.dingtalk.model;

import com.kuma.boot.common.utils.log.LogUtils;
import com.kuma.boot.dingtalk.annatations.DingerImageText;
import com.kuma.boot.dingtalk.annatations.DingerLink;
import com.kuma.boot.dingtalk.entity.DingerMethod;
import com.kuma.boot.dingtalk.enums.ExceptionEnum;
import com.kuma.boot.dingtalk.enums.MessageMainType;
import com.kuma.boot.dingtalk.enums.MessageSubType;
import com.kuma.boot.dingtalk.exception.DingerException;
import com.kuma.boot.dingtalk.utils.DingerUtils;
import com.kuma.boot.dingtalk.utils.XmlUtils;
import com.kuma.boot.dingtalk.xml.BeanTag;
import com.kuma.boot.dingtalk.xml.MessageTag;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

public class XmlDingerDefinitionResolver
extends AbstractDingerDefinitionResolver<Resource[]> {
    @Override
    public void resolver(Resource[] resources) {
        for (Resource resource : resources) {
            Class<?> dingerClass;
            String xml;
            if (!resource.isReadable()) {
                LogUtils.debug((String)"Ignored because not readable: {} ", (Object[])new Object[]{resource.getFilename()});
                continue;
            }
            try {
                xml = new String(FileCopyUtils.copyToByteArray((InputStream)resource.getInputStream()), "UTF-8");
            }
            catch (IOException e) {
                throw new DingerException(ExceptionEnum.RESOURCE_CONFIG_EXCEPTION, resource.getFilename());
            }
            xml = this.transferXml(xml);
            BeanTag dingerBean = XmlUtils.xmlToJavaBean(xml, BeanTag.class);
            if (dingerBean == null) {
                LogUtils.debug((String)"dinger xml file: {} content is empty.", (Object[])new Object[]{resource.getFilename()});
                continue;
            }
            String namespace = dingerBean.getNamespace();
            try {
                dingerClass = Class.forName(namespace);
            }
            catch (ClassNotFoundException e) {
                throw new DingerException(ExceptionEnum.DINER_XML_NAMESPACE_INVALID, namespace);
            }
            Map<String, DingerMethod> dingerClassMethods = this.dingerClassMethods(dingerClass);
            DingerConfig dingerConfiguration = this.dingerConfiguration(dingerClass);
            List<MessageTag> messages = dingerBean.getMessages();
            for (MessageTag message : messages) {
                String methodName = message.getIdentityId();
                if (!dingerClassMethods.containsKey(methodName)) {
                    LogUtils.debug((String)"namespace={}, messageId={} undefined in dingerClass.", (Object[])new Object[]{namespace, methodName});
                    continue;
                }
                String dingerName = namespace + "." + methodName;
                String messageSubType = message.getDingerType();
                if (!MessageSubType.contains(messageSubType)) {
                    throw new DingerException(ExceptionEnum.DINER_XML_MSGTYPE_INVALID, dingerName, messageSubType);
                }
                String dingerDefinitionKey = String.valueOf((Object)MessageMainType.XML) + "." + message.getDingerType();
                this.registerDingerDefinition(dingerName, message, dingerDefinitionKey, dingerConfiguration, dingerClassMethods.get(methodName));
            }
        }
    }

    protected Map<String, DingerMethod> dingerClassMethods(Class<?> dingerClass) {
        Method[] methods = dingerClass.getMethods();
        HashMap<String, DingerMethod> dingerMethodParams = new HashMap<String, DingerMethod>();
        for (Method method : methods) {
            String methodName = method.getName();
            String methodAllName = dingerClass.getSimpleName() + "." + methodName;
            int[] paramTypes = null;
            if (method.isAnnotationPresent(DingerImageText.class)) {
                paramTypes = DingerUtils.methodParamsGenericType(method, DingerImageText.clazz);
                if (paramTypes.length != 1) {
                    throw new DingerException(ExceptionEnum.IMAGETEXT_METHOD_PARAM_EXCEPTION, methodAllName);
                }
            } else if (method.isAnnotationPresent(DingerLink.class) && (paramTypes = DingerUtils.methodParamsType(method, DingerLink.clazz)).length != 1) {
                throw new DingerException(ExceptionEnum.LINK_METHOD_PARAM_EXCEPTION, methodAllName);
            }
            String[] methodParams = this.parameterNameDiscoverer.getParameterNames(method);
            dingerMethodParams.put(methodName, new DingerMethod(methodAllName, methodParams, paramTypes));
        }
        return dingerMethodParams;
    }

    String transferXml(String sourceXml) {
        return sourceXml.replaceAll("<!DOCTYPE.*>", "");
    }
}

