import 'dart:io';

class CpuInfo {
  late final String cpuModelName;

  CpuInfo() {
    final lines = File('/proc/cpuinfo').readAsLinesSync();
    final model = lines.firstWhere((line) => line.startsWith('model name'));
    cpuModelName = model.split(':').last.trim();
  }
}
