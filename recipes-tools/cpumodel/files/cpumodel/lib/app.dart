import 'dart:io';

class CpuInfo {
  late final String cpuModelName;

  CpuInfo() {
    final lines = File('/proc/cpuinfo').readAsLinesSync();
    final model = lines.firstWhere((line) => line.startsWith(RegExp(r'(?:model name|Model)\s+:')));
    cpuModelName = model.split(':').last.trim();
  }
}
