package com.kuma.boot.monitor.strategy;

import com.kuma.boot.monitor.model.Report;

public interface WarnStrategy {
   Report analyse(Report report);

   String analyseText(Report report);
}
