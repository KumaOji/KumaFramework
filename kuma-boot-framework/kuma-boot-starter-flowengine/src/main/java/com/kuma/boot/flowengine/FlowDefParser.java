package com.kuma.boot.flowengine;

import com.kuma.boot.flowengine.exception.FlowException;
import com.kuma.boot.flowengine.module.ActivityNode;
import com.kuma.boot.flowengine.module.Condition;
import com.kuma.boot.flowengine.module.EndNode;
import com.kuma.boot.flowengine.module.ErrorMonitor;
import com.kuma.boot.flowengine.module.EventListener;
import com.kuma.boot.flowengine.module.EventListeners;
import com.kuma.boot.flowengine.module.Flow;
import com.kuma.boot.flowengine.module.FlowNode;
import com.kuma.boot.flowengine.module.FlowRef;
import com.kuma.boot.flowengine.module.NodeRef;
import com.kuma.boot.flowengine.module.NodeType;
import com.kuma.boot.flowengine.module.RetryNode;
import com.kuma.boot.flowengine.module.StandardActivityNode;
import com.kuma.boot.flowengine.module.StartNode;
import com.kuma.boot.flowengine.module.Transition;
import com.kuma.boot.flowengine.state.retry.RetryFailTypeEnum;
import com.kuma.boot.flowengine.state.retry.RetryTransitionListener;
import com.kuma.boot.common.utils.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FlowDefParser {

   public static final String NAME_ATTRIBUTE = "name";
   public static final String VERSION_ATTRIBUTE = "version";
   public static final String TRIGGERS_CLASS_ATTRIBUTE = "triggers";
   public static final String LOG_NAME = "log_name";
   private static final String ERROR_MONITOR = "monitor";
   private static final String DESCRIPTION = "description";
   private static final String ERROR_MONITOR_ELEMENT_CLASS_ATTRIBUTE = "monitor_class";
   private static final String EVENT_LISTENERS = "event_listeners";
   private static final String EVENT_LISTENER = "listener";
   private static final String EVENT_LISTENER_ATTRIBUTE_CLASS = "class";
   private static final String EVENT_LISTENER_ATTRIBUTE_DESCRIPTION = "description";
   public static final String EXCEPTION_MAPPINGS = "exception_mappings";
   public static final String EXCEPTION_MAPPING = "exception_mapping";
   public static final String EXCEPTION_CLASS = "exception_class";
   public static final String COMMON_NAME_ATTRIBUTE = "name";
   public static final String COMMON_TRIGGER_CLASS_ATTRIBUTE = "trigger_class";
   public static final String COMMON_TRANCE_LOG = "trace_log";
   public static final String TRANSITION_ELEMENT = "transition";
   private static final String TRANSITION_ELEMENT_EVENT_ATTRIBUTE = "event";
   public static final String TRANSITION_ELEMENT_DESCRIPTION_ATTRIBUTE = "description";
   public static final String TRANSITION_ELEMENT_TO_ATTRIBUTE = "to";
   public static final String CONDITION_ELEMENT = "condition";
   public static final String CONDITION_ELEMENT_SCRIPT_ATTRIBUTE = "mvel_script";
   public static final String START_ELEMENT = "start";
   public static final String END_ELEMENT = "end";
   public static final String AUTO_ELEMENT = "auto_task";
   public static final String ACTIVE_ELEMENT = "active_node";
   public static final String RETRY_ELEMENT = "retry_task";
   public static final String RETRY_TARGET = "target";
   public static final String RETRY_FAIL_ATTRIBUTE = "retryFail";
   public static final String RETRY_INFO_ATTRIBUTE = "retryInfo";
   public static final String RETRY_MAX_LIMIT_NODE_ATTRIBUTE = "retryMaxLimitNode";
   public static final String SUB_FLOW_ELEMENT = "sub_flow";
   public static final String SUB_FLOW_ELEMENT_REFNAME_ATTRIBUTE = "sub_flow_name";
   public static final String SUB_FLOW_ELEMENT_NAME_ATTRIBUTE = "name";
   public static final String SUB_FLOW_ELEMENT_VERSION_ATTRIBUTE = "version";


   public Flow parse(Element rootElement) {
      //－1.流程创建属性赋值
      Flow flow = flowAssignment(rootElement);

      //－2.节点分析
      NodeList nodeList = rootElement.getChildNodes();
      for (int i = 0, j = nodeList.getLength(); i < j; i++) {
         Node node = nodeList.item(i);
         if (node.getNodeType() == Element.ELEMENT_NODE) {
            NodeCreator.creator(node.getLocalName()).create(flow, (Element) node);
         }
      }

      //重新初始化重试节点
      boolean hasRetry = reInitRetryNode(flow);

      //添加重试监听
      if (hasRetry) {
         reInitRetryListen(flow);
      }
      return flow;
   }


   private boolean reInitRetryNode(Flow flow) {
      boolean hasRetry = false;
      //动态初始化处理重试节点
      for (ActivityNode node : flow.getNodes()) {
         if (node instanceof RetryNode) {
            RetryNode retryNode = (RetryNode) node;
            //动态初始化处理重试节点
            retryNode.addRetryInit(flow);

            hasRetry = true;
         }
      }
      return hasRetry;
   }

   private void reInitRetryListen(Flow flow) {
      if (null == flow.getEventListeners()) {
         flow.setEventListeners(new EventListeners());
      }

      EventListener retryListener = new EventListener(RetryTransitionListener.class.getName(),
              "重试监听");
      flow.getEventListeners().addListener(retryListener);
      flow.addEvent(RetryNode.RETRY_EXIT_EVENT);
   }

   private Flow flowAssignment(Element rootElement) {
      Flow flow = new Flow();

      String name = rootElement.getAttribute(NAME_ATTRIBUTE);
      int version = Integer.parseInt(rootElement.getAttribute(VERSION_ATTRIBUTE));
      String triggers = rootElement.getAttribute(TRIGGERS_CLASS_ATTRIBUTE);

      flow.setName(name);
      flow.setVersion(version);
      flow.setTriggerClass(triggers);

      String logName = rootElement.getAttribute(LOG_NAME);
      if (StringUtils.isBlank(logName)) {
         logName = Flow.class.getName();
      }
      flow.setLogName(logName);

      return flow;
   }

   enum NodeCreator {
      start_node(START_ELEMENT) {
         @Override
         void create(Flow flow, Element startElement) {
            //－1.构建开始节点
            StartNode startNode = new StartNode();
            flow.setStartNode(startNode);

            startNode.setName(startElement.getAttribute(COMMON_NAME_ATTRIBUTE));
            startNode.setTriggerClass(
                    startElement.getAttribute(COMMON_TRIGGER_CLASS_ATTRIBUTE));

            startNode.setTraceLog(Boolean.parseBoolean(COMMON_TRANCE_LOG));

            //－2.分析transition并构建Condition(开始节点不包含Condition)以及ErrorPolicy覆盖
            analyze(startElement, startNode, flow);
         }
      },

      end_node(END_ELEMENT) {
         @Override
         void create(Flow flow, Element endElement) {
            String name = endElement.getAttribute(COMMON_NAME_ATTRIBUTE);
            String triggerClass = endElement.getAttribute(COMMON_TRIGGER_CLASS_ATTRIBUTE);

            EndNode endNode = new EndNode();
            endNode.setTriggerClass(triggerClass);
            endNode.setName(name);
            endNode.setTraceLog(Boolean.parseBoolean(COMMON_TRANCE_LOG));

            flow.setEndNode(endNode);
         }
      },

      event_listeners(EVENT_LISTENERS) {
         @Override
         void create(Flow flow, Element node) {
            EventListeners eventListeners = new EventListeners();
            flow.setEventListeners(eventListeners);

            NodeList listenerElements = node.getElementsByTagName(EVENT_LISTENER);
            for (int i = 0, j = listenerElements.getLength(); i < j; i++) {
               Node listenerNode = listenerElements.item(i);
               if (listenerNode.getNodeType() == Element.ELEMENT_NODE
                       && listenerNode.getLocalName().equals(EVENT_LISTENER)) {
                  Element listenerElement = (Element) listenerNode;

                  EventListener eventListener = new EventListener(
                          listenerElement.getAttribute(EVENT_LISTENER_ATTRIBUTE_CLASS),
                          listenerElement.getAttribute(EVENT_LISTENER_ATTRIBUTE_DESCRIPTION)
                  );
                  eventListeners.addListener(eventListener);
               }
            }
         }
      },

      error_monitor(ERROR_MONITOR) {
         @Override
         void create(Flow flow, Element errorPolicyElement) {
            analyzeErrorMonitor(flow, errorPolicyElement);
         }
      },

      description(DESCRIPTION) {
         @Override
         void create(Flow flow, Element nodeElement) {
            flow.setDescription(nodeElement.getTextContent());
         }
      },

      standard_node("APP_KIT_FLOW_STANDARD") {
         @Override
         void create(Flow flow, Element nodeElement) {
            StandardActivityNode standardActivityNode = new StandardActivityNode();
            initStandardNode(standardActivityNode, flow, nodeElement);

            // 分析Condition、Transition
            analyze(nodeElement, standardActivityNode, flow);
         }
      },

      auto_task(AUTO_ELEMENT) {
         @Override
         void create(Flow flow, Element activeElement) {
            standard_node.create(flow, activeElement);
         }
      },

      active_node(ACTIVE_ELEMENT) {
         @Override
         void create(Flow flow, Element activeElement) {
            standard_node.create(flow, activeElement);
         }
      },

      retry_task(RETRY_ELEMENT) {
         @Override
         void create(Flow flow, Element nodeElement) {
            RetryNode retryNode = new RetryNode();
            initStandardNode(retryNode, flow, nodeElement);

            //直接固定重试触发动作
            retryNode.setTarget(nodeElement.getAttribute(RETRY_TARGET));
            retryNode.setTriggerClass(nodeElement.getAttribute(COMMON_TRIGGER_CLASS_ATTRIBUTE));
            retryNode.setRetryMaxLimitNode(
                    nodeElement.getAttribute(RETRY_MAX_LIMIT_NODE_ATTRIBUTE));
            retryNode.setRetryInfo(nodeElement.getAttribute(RETRY_INFO_ATTRIBUTE));
            retryNode.setRetryFailType(
                    RetryFailTypeEnum.getByCode(nodeElement.getAttribute(RETRY_FAIL_ATTRIBUTE)));

         }
      },

      sub_fLow(SUB_FLOW_ELEMENT) {
         @Override
         void create(Flow flow, Element subFLowElement) {
            String refName = subFLowElement.getAttribute(SUB_FLOW_ELEMENT_REFNAME_ATTRIBUTE);
            String name = subFLowElement.getAttribute(SUB_FLOW_ELEMENT_NAME_ATTRIBUTE);

            int version = Integer.parseInt(
                    subFLowElement.getAttribute(SUB_FLOW_ELEMENT_VERSION_ATTRIBUTE));

            FlowRef flowRef = new FlowRef(name, refName, version);
            analyze(subFLowElement, flowRef, flow);
            flow.addNode(flowRef);
         }
      };

      void initStandardNode(StandardActivityNode standardActivityNode, Flow flow,
                            Element nodeElement) {
         standardActivityNode.setNodeType(NodeType.get(nodeElement.getLocalName()));
         standardActivityNode.setName(nodeElement.getAttribute(COMMON_NAME_ATTRIBUTE));
         standardActivityNode.setTriggerClass(
                 nodeElement.getAttribute(COMMON_TRIGGER_CLASS_ATTRIBUTE));
         standardActivityNode.setTraceLog(
                 Boolean.parseBoolean(nodeElement.getAttribute(COMMON_TRANCE_LOG)));

         flow.addNode(standardActivityNode);
      }

      void analyze(Element element, FlowNode from, Flow flow) {
         NodeList nodeList = element.getChildNodes();
         for (int i = 0, j = nodeList.getLength(); i < j; i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Element.ELEMENT_NODE) {
               // ActivityNode子类
               analyzeCondition(node, (ActivityNode) from, flow);
            }
         }
      }

      void analyzeCondition(Node node, ActivityNode from, Flow flow) {
         Element element = (Element) node;
         Condition condition = new Condition();

         if (element.getLocalName().equals(CONDITION_ELEMENT)) {
            condition.setMvelScript(element.getAttribute(CONDITION_ELEMENT_SCRIPT_ATTRIBUTE));
            NodeList transitionNodes = element.getElementsByTagName(TRANSITION_ELEMENT);

            if (transitionNodes == null || transitionNodes.getLength() == 0) {
               throw new FlowException(
                       String.format("FLow=%s,Version=%s,Node=%s定义条件出错,没有正确定义transition属性"
                               , flow.getName(), flow.getVersion(), from.getName()));
            }
            for (int x = 0, y = transitionNodes.getLength(); x < y; x++) {
               Element transitionElement = (Element) transitionNodes.item(x);
               buildCondition(flow, condition, from, transitionElement);
            }
         }
         else {
            //针对只有一条transition连线的节点,直接执行即可。
            buildCondition(flow, condition, from, element);
         }
         from.setCondition(condition);
      }

      void buildCondition(Flow flow, Condition condition, ActivityNode from, Element element) {
         Transition transition = new Transition();
         transition.setDescription(
                 element.getAttribute(TRANSITION_ELEMENT_DESCRIPTION_ATTRIBUTE));
         String event = element.getAttribute(TRANSITION_ELEMENT_EVENT_ATTRIBUTE);
         transition.setEvent(event);
         flow.addEvent(event);
         transition.setFrom(from);
         NodeRef nodeRef = new NodeRef(element.getAttribute(TRANSITION_ELEMENT_TO_ATTRIBUTE));
         transition.setTo(nodeRef);
         condition.addTransition(transition);

      }

      void analyzeErrorMonitor(Flow flow, Node node) {
         Element errorMonitorElement = (Element) node;

         ErrorMonitor errorMonitor = new ErrorMonitor();
         flow.setErrorMonitor(errorMonitor);

         errorMonitor.setErrorMonitorClass(
                 errorMonitorElement.getAttribute(ERROR_MONITOR_ELEMENT_CLASS_ATTRIBUTE));

         Element excpMappingElement = (Element) errorMonitorElement.getElementsByTagName(
                 EXCEPTION_MAPPINGS).item(0);
         NodeList exceptionsNode = excpMappingElement.getChildNodes();

         if (exceptionsNode.getLength() > 0) {
            for (int i = 0, j = exceptionsNode.getLength(); i < j; i++) {
               Node exceptionNo = exceptionsNode.item(i);
               if (exceptionNo.getNodeType() == Element.ELEMENT_NODE
                       && exceptionNo.getLocalName().equals(EXCEPTION_MAPPING)) {
                  Element exceptionElement = (Element) exceptionNo;
                  errorMonitor.getExceptionMapping()
                          .addThrowable(exceptionElement.getAttribute(EXCEPTION_CLASS));
               }
            }
         }
      }

      private String elementName;

      private NodeCreator(String elementName) {
         this.elementName = elementName;
      }

      public static FlowDefParser.NodeCreator creator(String elementName) {
         FlowDefParser.NodeCreator creator = null;
         for (NodeCreator f : values()) {
            if (elementName.equals(f.elementName)) {
               creator = f;
               break;
            }

            if (creator == null) {
               creator = standard_node;
            }


         }
         return creator;
      }

      abstract void create(Flow flow, Element nodeElement);

   }
}

