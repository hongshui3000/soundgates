// Generated by CoffeeScript 1.7.1
(function() {
  var expand, expandComponent, generateTestdata, getComponentList, getExecution, getExecutionList, getFormattedTime, getLastExecution, initComponentNavigation, initExecutionNavigation, initializeButtons, initializeDocument, isInt, print, readyFn, removeEveryExecution, removeExecution, __calcAverageExecutionTime, __calcAverageExecutionTimeList, __calcTypeImplementationDistribution, __createDiagram, __getJsDate, __prepareSamples, __replaceRaute;

  readyFn = function(jQuery) {
    console.log("Ready.");
    initializeDocument();
    initializeButtons();
  };

  initializeDocument = function() {
    $("[data-clampedwidth]").each(function() {
      var elem, parentPanel, resizeFn;
      elem = $(this);
      parentPanel = elem.data("clampedwidth");
      console.dir(parentPanel);
      resizeFn = function() {
        var sideBarNavWidth;
        sideBarNavWidth = $(parentPanel).width();
        elem.css("width", sideBarNavWidth);
        console.log("Setting width to: " + sideBarNavWidth);
      };
      resizeFn();
      $(window).resize(resizeFn);
    });
    $("#component_average_execution").parent().parent().css("margin-left", "0px");
    getExecutionList().done(initExecutionNavigation);
    return getLastExecution().done(expand);
  };

  initExecutionNavigation = function(executions) {
    var exec, li, nav, _i, _len, _results;
    nav = $("#nav_executions").empty();
    $('<li class="nav-header">').appendTo(nav).text("Executions");
    _results = [];
    for (_i = 0, _len = executions.length; _i < _len; _i++) {
      exec = executions[_i];
      exec.formattedTime = getFormattedTime(exec.timestamp);
      li = $('<li>').appendTo(nav);
      _results.push($('<a href="#">').appendTo(li).text(exec.formattedTime).fadeIn());
    }
    return _results;
  };

  initComponentNavigation = function(components) {
    var c, el, li, nav, _i, _len, _results;
    nav = $("#nav_components").empty();
    $('<li class="nav-header">').appendTo(nav).text("Components");
    _results = [];
    for (_i = 0, _len = components.length; _i < _len; _i++) {
      c = components[_i];
      li = $('<li>').appendTo(nav);
      el = $('<a href="#">').appendTo(li).text(c.uid).fadeIn();
      _results.push(li.click(function() {
        var target;
        target = '#' + __replaceRaute($(this).children('a').text());
        console.log("Scrolling to " + target);
        return $("html, body").animate({
          scrollTop: $(target).position().top
        }, "slow");
      }));
    }
    return _results;
  };

  initializeButtons = function() {
    $("#refresh_test").click(function() {
      $("html, body").animate({
        scrollTop: $('home').position().top
      }, "slow");
    });
    $("#refresh_test").click(getExecutionList);
    $("#generate_test").click(generateTestdata);
    $("#remove_test").click(removeEveryExecution);
  };

  expand = function(execution) {
    var c, expand_addAverageExuction, expand_addComponentCount, expand_addDate, expand_addImplementationDistribution, _i, _len, _ref;
    getComponentList(execution.timestamp).done(initComponentNavigation);
    expand_addDate = function(execution) {
      var date, div, _getDate, _getHour;
      date = __getJsDate(execution.timestamp);
      _getHour = function(d) {
        return d.getHours() + ":" + d.getMinutes();
      };
      _getDate = function(d) {
        var monthNames;
        monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];
        return d.getDate() + '/' + monthNames[d.getMonth()] + '/' + d.getFullYear();
      };
      div = $('#execution_date');
      $('<h1 class="text-right"/>').text(_getHour(date)).css("font-weight", "Bold").appendTo(div);
      $('<p class="text-right"/>').text(_getDate(date)).appendTo(div);
    };
    expand_addComponentCount = function(execution) {
      var count, div;
      count = execution.components.length;
      div = $('#component_count');
      $('<h1/>').text('#' + count).css("font-weight", "Bold").appendTo(div);
      $('<p/>').text('have been found').appendTo(div);
    };
    expand_addImplementationDistribution = function(execution) {
      nv.addGraph(function() {
        var data, div, pie;
        pie = nv.models.pieChart().x(function(d) {
          return d.label;
        }).y(function(d) {
          return d.value;
        }).showLabels(false).labelThreshold(.05).labelType("percent").donut(true).donutRatio(0.5).color(['steelblue', 'lightgreen']);
        data = __calcTypeImplementationDistribution(execution.components);
        div = '#component_implementations';
        d3.select(div).append('svg').datum(data).transition().duration(350).call(pie);
        return pie;
      });
    };
    expand_addAverageExuction = function() {
      nv.addGraph(function() {
        var bar, data, div;
        bar = nv.models.discreteBarChart().x(function(d) {
          return d.label;
        }).y(function(d) {
          return d.value;
        }).staggerLabels(true).tooltips(false).showValues(true).transitionDuration(350);
        div = '#component_average_execution';
        data = __calcAverageExecutionTimeList(execution.components);
        d3.select(div).append('svg').attr('height', 200).datum(data).transition().duration(350).call(bar);
        return bar;
      });
    };
    expand_addDate(execution);
    expand_addComponentCount(execution);
    expand_addImplementationDistribution(execution);
    expand_addAverageExuction(execution);
    _ref = execution.components;
    for (_i = 0, _len = _ref.length; _i < _len; _i++) {
      c = _ref[_i];
      expandComponent(c);
    }
  };

  expandComponent = function(component) {
    var avg, body, card, row, title, typ;
    row = $('<div class="span12"/>').attr("id", __replaceRaute(component.uid)).css("margin-left", "0px").appendTo('#execution');
    card = $('<div class="card"/>').appendTo(row);
    title = $('<h2 class="card-heading"/>').appendTo(card).text('UID: ' + component.uid);
    body = $('<div class="card-body"/>').appendTo(card);
    typ = $('<p/>').appendTo(body).text('Type: ' + component.type);
    avg = $('<p/>').appendTo(body).text('Average Execution time: ' + __calcAverageExecutionTime(component.execution_times));
    $('<div/>').appendTo(body).attr('id', __replaceRaute(component.uid) + "_graphic");
    __createDiagram(component);
  };

  __calcTypeImplementationDistribution = function(components) {
    var c, hw, hw_percentage, result, sw, sw_components, sw_percentage, _i, _len;
    result = [];
    sw_components = 0;
    for (_i = 0, _len = components.length; _i < _len; _i++) {
      c = components[_i];
      if (c.type === 'SW' || c.type === 'sw' || c.type === 'Sw') {
        sw_components += 1;
      }
    }
    sw_percentage = sw_components > 0 ? sw_components * 100 / components.length : 0;
    hw_percentage = 100 - sw_percentage;
    sw = {
      label: "SW",
      value: sw_percentage
    };
    result.push(sw);
    hw = {
      label: "HW",
      value: hw_percentage
    };
    result.push(hw);
    return result;
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

  __calcAverageExecutionTimeList = function(components) {
    var c, data, result, _i, _len;
    result = [
      {
        key: "Average Execution Time",
        values: []
      }
    ];
    for (_i = 0, _len = components.length; _i < _len; _i++) {
      c = components[_i];
      data = {
        label: __replaceRaute(c.uid),
        value: __calcAverageExecutionTime(c.execution_times)
      };
      result[0].values.push(data);
    }
    return result;
  };

  __createDiagram = function(component) {
    var draw_samples;
    console.log("Info: Creating compnent diagram for " + component.uid);
    draw_samples = __prepareSamples(component);
    return nv.addGraph(function() {
      var chart, selector;
      chart = nv.models.lineChart().margin({
        left: 20
      }).useInteractiveGuideline(true).transitionDuration(350).showLegend(true).showYAxis(true).showXAxis(true);
      chart.xAxis.axisLabel("Samples").tickFormat(d3.format(",r"));
      chart.yAxis.axisLabel("Amplitude").tickFormat(d3.format("5.00f"));
      selector = __replaceRaute(component.uid);
      selector = '#' + selector + '_graphic';
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

  __replaceRaute = function(text) {
    return text.replace(/#/g, '_');
  };

  getLastExecution = function() {
    return $.ajax({
      url: '/execution/latest',
      type: "GET"
    });
  };

  getComponentList = function(timestamp) {
    return $.ajax({
      url: '/componentlist/' + timestamp,
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

  getExecutionList = function() {
    return $.ajax({
      url: '/execution/list',
      type: "GET"
    });
  };

  generateTestdata = function() {
    return $.ajax({
      url: '/generate',
      type: "GET"
    });
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
