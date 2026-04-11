package com.kuma.boot.flowengine;

import com.kuma.boot.common.utils.lang.StringUtils;
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

   public FlowDefParser() {
   }

   public Flow parse(Element rootElement) {
      Flow flow = this.flowAssignment(rootElement);
      NodeList nodeList = rootElement.getChildNodes();
      int i = 0;

      for(int j = nodeList.getLength(); i < j; ++i) {
         Node node = nodeList.item(i);
         if (node.getNodeType() == 1) {
            FlowDefParser.NodeCreator.creator(node.getLocalName()).create(flow, (Element)node);
         }
      }

      i = this.reInitRetryNode(flow);
      if (i) {
         this.reInitRetryListen(flow);
      }

      return flow;
   }

   private boolean reInitRetryNode(Flow flow) {
      boolean hasRetry = false;

      for(ActivityNode node : flow.getNodes()) {
         if (node instanceof RetryNode retryNode) {
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

      EventListener retryListener = new EventListener(RetryTransitionListener.class.getName(), "\u91cd\u8bd5\u76d1\u542c");
      flow.getEventListeners().addListener(retryListener);
      flow.addEvent("retry_exit");
   }

   private Flow flowAssignment(Element rootElement) {
      Flow flow = new Flow();
      String name = rootElement.getAttribute("name");
      int version = Integer.parseInt(rootElement.getAttribute("version"));
      String triggers = rootElement.getAttribute("triggers");
      flow.setName(name);
      flow.setVersion(version);
      flow.setTriggerClass(triggers);
      String logName = rootElement.getAttribute("log_name");
      if (StringUtils.isBlank(logName)) {
         logName = Flow.class.getName();
      }

      flow.setLogName(logName);
      return flow;
   }

   static enum NodeCreator {
      start_node("start") {
         void create(Flow flow, Element startElement) {
            StartNode startNode = new StartNode();
            flow.setStartNode(startNode);
            startNode.setName(startElement.getAttribute("name"));
            startNode.setTriggerClass(startElement.getAttribute("trigger_class"));
            startNode.setTraceLog(Boolean.parseBoolean("trace_log"));
            this.analyze(startElement, startNode, flow);
         }
      },
      end_node("end") {
         void create(Flow flow, Element endElement) {
            String name = endElement.getAttribute("name");
            String triggerClass = endElement.getAttribute("trigger_class");
            EndNode endNode = new EndNode();
            endNode.setTriggerClass(triggerClass);
            endNode.setName(name);
            endNode.setTraceLog(Boolean.parseBoolean("trace_log"));
            flow.setEndNode(endNode);
         }
      },
      event_listeners("event_listeners") {
         void create(Flow flow, Element node) {
            EventListeners eventListeners = new EventListeners();
            flow.setEventListeners(eventListeners);
            NodeList listenerElements = node.getElementsByTagName("listener");
            int i = 0;

            for(int j = listenerElements.getLength(); i < j; ++i) {
               Node listenerNode = listenerElements.item(i);
               if (listenerNode.getNodeType() == 1 && listenerNode.getLocalName().equals("listener")) {
                  Element listenerElement = (Element)listenerNode;
                  EventListener eventListener = new EventListener(listenerElement.getAttribute("class"), listenerElement.getAttribute("description"));
                  eventListeners.addListener(eventListener);
               }
            }

         }
      },
      error_monitor("monitor") {
         void create(Flow flow, Element errorPolicyElement) {
            this.analyzeErrorMonitor(flow, errorPolicyElement);
         }
      },
      description("description") {
         void create(Flow flow, Element nodeElement) {
            flow.setDescription(nodeElement.getTextContent());
         }
      },
      standard_node("APP_KIT_FLOW_STANDARD") {
         void create(Flow flow, Element nodeElement) {
            StandardActivityNode standardActivityNode = new StandardActivityNode();
            this.initStandardNode(standardActivityNode, flow, nodeElement);
            this.analyze(nodeElement, standardActivityNode, flow);
         }
      },
      auto_task("auto_task") {
         void create(Flow flow, Element activeElement) {
            standard_node.create(flow, activeElement);
         }
      },
      active_node("active_node") {
         void create(Flow flow, Element activeElement) {
            standard_node.create(flow, activeElement);
         }
      },
      retry_task("retry_task") {
         void create(Flow flow, Element nodeElement) {
            RetryNode retryNode = new RetryNode();
            this.initStandardNode(retryNode, flow, nodeElement);
            retryNode.setTarget(nodeElement.getAttribute("target"));
            retryNode.setTriggerClass(nodeElement.getAttribute("trigger_class"));
            retryNode.setRetryMaxLimitNode(nodeElement.getAttribute("retryMaxLimitNode"));
            retryNode.setRetryInfo(nodeElement.getAttribute("retryInfo"));
            retryNode.setRetryFailType(RetryFailTypeEnum.getByCode(nodeElement.getAttribute("retryFail")));
         }
      },
      sub_fLow("sub_flow") {
         void create(Flow flow, Element subFLowElement) {
            String refName = subFLowElement.getAttribute("sub_flow_name");
            String name = subFLowElement.getAttribute("name");
            int version = Integer.parseInt(subFLowElement.getAttribute("version"));
            FlowRef flowRef = new FlowRef(name, refName, version);
            this.analyze(subFLowElement, flowRef, flow);
            flow.addNode(flowRef);
         }
      };

      private String elementName;

      void initStandardNode(StandardActivityNode standardActivityNode, Flow flow, Element nodeElement) {
         standardActivityNode.setNodeType(NodeType.get(nodeElement.getLocalName()));
         standardActivityNode.setName(nodeElement.getAttribute("name"));
         standardActivityNode.setTriggerClass(nodeElement.getAttribute("trigger_class"));
         standardActivityNode.setTraceLog(Boolean.parseBoolean(nodeElement.getAttribute("trace_log")));
         flow.addNode(standardActivityNode);
      }

      void analyze(Element element, FlowNode from, Flow flow) {
         NodeList nodeList = element.getChildNodes();
         int i = 0;

         for(int j = nodeList.getLength(); i < j; ++i) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == 1) {
               this.analyzeCondition(node, (ActivityNode)from, flow);
            }
         }

      }

      void analyzeCondition(Node node, ActivityNode from, Flow flow) {
         Element element = (Element)node;
         Condition condition = new Condition();
         if (element.getLocalName().equals("condition")) {
            condition.setMvelScript(element.getAttribute("mvel_script"));
            NodeList transitionNodes = element.getElementsByTagName("transition");
            if (transitionNodes == null || transitionNodes.getLength() == 0) {
               throw new FlowException(String.format("FLow=%s,Version=%s,Node=%s\u5b9a\u4e49\u6761\u4ef6\u51fa\u9519,\u6ca1\u6709\u6b63\u786e\u5b9a\u4e49transition\u5c5e\u6027", flow.getName(), flow.getVersion(), from.getName()));
            }

            int x = 0;

            for(int y = transitionNodes.getLength(); x < y; ++x) {
               Element transitionElement = (Element)transitionNodes.item(x);
               this.buildCondition(flow, condition, from, transitionElement);
            }
         } else {
            this.buildCondition(flow, condition, from, element);
         }

         from.setCondition(condition);
      }

      void buildCondition(Flow flow, Condition condition, ActivityNode from, Element element) {
         Transition transition = new Transition();
         transition.setDescription(element.getAttribute("description"));
         String event = element.getAttribute("event");
         transition.setEvent(event);
         flow.addEvent(event);
         transition.setFrom(from);
         NodeRef nodeRef = new NodeRef(element.getAttribute("to"));
         transition.setTo(nodeRef);
         condition.addTransition(transition);
      }

      void analyzeErrorMonitor(Flow flow, Node node) {
         Element errorMonitorElement = (Element)node;
         ErrorMonitor errorMonitor = new ErrorMonitor();
         flow.setErrorMonitor(errorMonitor);
         errorMonitor.setErrorMonitorClass(errorMonitorElement.getAttribute("monitor_class"));
         Element excpMappingElement = (Element)errorMonitorElement.getElementsByTagName("exception_mappings").item(0);
         NodeList exceptionsNode = excpMappingElement.getChildNodes();
         if (exceptionsNode.getLength() > 0) {
            int i = 0;

            for(int j = exceptionsNode.getLength(); i < j; ++i) {
               Node exceptionNo = exceptionsNode.item(i);
               if (exceptionNo.getNodeType() == 1 && exceptionNo.getLocalName().equals("exception_mapping")) {
                  Element exceptionElement = (Element)exceptionNo;
                  errorMonitor.getExceptionMapping().addThrowable(exceptionElement.getAttribute("exception_class"));
               }
            }
         }

      }

      private NodeCreator(String elementName) {
         this.elementName = elementName;
      }

      public static NodeCreator creator(String elementName) {
         NodeCreator creator = null;

         for(NodeCreator f : values()) {
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

      // $FF: synthetic method
      private static NodeCreator[] $values() {
         return new NodeCreator[]{start_node, end_node, event_listeners, error_monitor, description, standard_node, auto_task, active_node, retry_task, sub_fLow};
      }
   }
}
