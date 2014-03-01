// Generated by CoffeeScript 1.7.1
(function() {
  var expand, expandComponent, getExecution, getFormattedTime, getLastExecution, isInt, print, readyFn, removeEveryExecution, removeExecution, __calcAverageExecutionTime, __createDiagram, __getJsDate, __prepareSamples, __replaceRaute;

  readyFn = function(jQuery) {
    var blah;
    console.log("Ready.");
    $("[data-toggle=offcanvas]").click(function() {
      console.log("Click on button ");
      $(".row-offcanvas").toggleClass("active");
    });
    getLastExecution().done(expand);
    blah = $("#list-group").children(".list-group-item").each((function(_this) {
      return function(index, element) {
        var value;
        value = $(element).attr("value");
        return console.log(value);
      };
    })(this));
  };

  expand = function(execution) {
    var c, row_1, row_2, row_3, _i, _len, _ref;
    execution.formattedTime = getFormattedTime(execution.timestamp);
    row_1 = $('<div class="row"></div>').appendTo('#execution');
    $('<div class="col-md-4"/>').appendTo(row_1).text("Execution date:");
    $('<div class="col-md-8"/>').appendTo(row_1).text(execution.formattedTime);
    row_2 = $('<div class="row"></div>').appendTo('#execution');
    $('<div class="col-md-4"/>').appendTo(row_2).text("Turnaround time:");
    $('<div class="col-md-8"/>').appendTo(row_2).text(execution.turnaround);
    row_3 = $('<div class="row"></div>').appendTo('#execution');
    $('<div class="col-md-4"/>').appendTo(row_3).text("Number of Components:");
    $('<div class="col-md-8"/>').appendTo(row_3).text(execution.components.length);
    row_3 = $('<br>').appendTo('#execution');
    _ref = execution.components;
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      c = _ref[_i];
      expandComponent(c);
    }
  };

  expandComponent = function(component) {
    var panel, typ;
    panel = $('<div class="panel panel-primary"></div>').appendTo('#execution');
    $('<div class="panel-heading"/>').appendTo(panel).text('UID: ' + component.uid);
    typ = $('<div class="panel-body"/>').appendTo(panel).text('Type: ' + component.type);
    $('<p/>').appendTo(typ).text('Average Execution time: ' + __calcAverageExecutionTime(component.execution_times));
    $('<div/>').appendTo(panel).attr('id', __replaceRaute(component.uid));
    __createDiagram(component);
  };

  __createDiagram = function(component) {
    var draw_samples;
    console.log("Info: Creating compnent diagram for " + component.uid);
    draw_samples = __prepareSamples(component);
    return nv.addGraph(function() {
      var chart, selector;
      console.dir(component);
      chart = nv.models.lineChart().margin({
        left: 20
      }).useInteractiveGuideline(true).transitionDuration(350).showLegend(true).showYAxis(true).showXAxis(true);
      chart.xAxis.axisLabel("Samples").tickFormat(d3.format(",r"));
      chart.yAxis.axisLabel("Amplitude").tickFormat(d3.format("5.00f"));
      selector = __replaceRaute(component.uid);
      selector = '#' + selector;
      d3.select(selector).append('svg').datum(draw_samples).call(chart);
      nv.utils.windowResize(chart.update);
      nv.utils.windowResize(function() {
        chart.update();
      });
      return chart;
    });
  };

  __prepareSamples = function(component) {
    var data, i, input_length, j, port, sample, _i, _j, _k, _l, _len, _len1, _len2, _len3, _ref, _ref1, _ref2, _ref3;
    data = [];
    console.dir(component.input_samples);
    input_length = component.input_samples.length;
    _ref = component.input_samples;
    for (i = _i = 0, _len = _ref.length; _i < _len; i = ++_i) {
      port = _ref[i];
      data.push({
        key: 'Input port ' + i,
        values: []
      });
      _ref1 = component.input_samples[i].values;
      for (j = _j = 0, _len1 = _ref1.length; _j < _len1; j = ++_j) {
        sample = _ref1[j];
        data[i].values.push({
          x: j,
          y: sample
        });
      }
    }
    _ref2 = component.output_samples;
    for (i = _k = 0, _len2 = _ref2.length; _k < _len2; i = ++_k) {
      port = _ref2[i];
      data.push({
        key: 'Output port ' + i,
        values: []
      });
      _ref3 = component.output_samples[i].values;
      for (j = _l = 0, _len3 = _ref3.length; _l < _len3; j = ++_l) {
        sample = _ref3[j];
        data[input_length + i].values.push({
          x: j,
          y: sample
        });
      }
    }
    return data;
  };

  __calcAverageExecutionTime = function(execution_times) {
    var result, time, _i, _len;
    if (execution_times.length === 0) {
      return "No data available";
    }
    result = 0;
    for (_i = 0, _len = execution_times.length; _i < _len; _i++) {
      time = execution_times[_i];
      result = result + time;
    }
    return result / execution_times.length;
  };

  __replaceRaute = function(text) {
    return text.replace(/#/g, '_');
  };

  getLastExecution = function() {
    return $.ajax({
      url: '/execution/latest',
      type: "GET"
    });
  };

  getExecution = function(timestamp) {
    if (isInt(timestamp)) {
      return $.ajax({
        url: '/execution/' + timestamp,
        type: "GET"
      });
    }
  };

  removeExecution = function(timestamp) {
    if (isInt(timestamp)) {
      return $.ajax({
        url: '/remove/' + timestamp,
        type: "GET"
      });
    }
  };

  removeEveryExecution = function() {
    return $.ajax({
      url: '/remove/all',
      type: "GET"
    });
  };

  print = function(data) {
    return console.dir(data);
  };

  isInt = function(number) {
    return typeof n === 'number' && n % 1 === 0;
  };

  getFormattedTime = function(x) {
    var date, datum, hours, minutes, month, seconds, year;
    date = __getJsDate(x);
    year = date.getFullYear();
    month = date.getMonth();
    datum = date.getDate();
    hours = date.getHours();
    minutes = date.getMinutes();
    seconds = date.getSeconds();
    return datum + '.' + month + '.' + year + ' ' + hours + ':' + minutes + ':' + seconds;
  };

  __getJsDate = function(x) {
    var date;
    date = new Date(x * 1000);
    return date;
  };

  $(document).ready(readyFn);

}).call(this);
