/*

Auto arguments completion tool by Tsun Winston Yeung (w@yeungtw.com).

An auto completion tool for quick filling-in default and user-defined values for arguments.
Optimized for UGens, may work with other classes, but not recommanded.

*/

AutoArgs {

	classvar func, exists=false;
	classvar <>autoStart;

	*initClass {

		if(Platform.ideName == "scqt",{
		(AutoArgs.filenameSymbol.asString.dirname +/+ "autoArgsSettings.scd").load;

		autoStart ?? {autoStart = true; this.saveSettings};

		if(autoStart,{{this.enable}.defer(2)});

		ShutDown.add({this.saveSettings;})

		});

	}

	*enable{

		if(exists == false){

			func = {|doc, char, modifiers, unicode, keycode|
				var expendFunc;

				if((unicode == 10)||(unicode == 11) && modifiers.isCtrl,
					{
						var currentPos, existingArgs, findCode;
						var ugen, method, methodEndPos;
						var start, end;

						currentPos = doc.selectionStart;

						findCode = {
							var pos, opCount=0, cpCount=0;
							var ugen_="", method_="";
							var existingArgsString = "", existingArgsAry, splitExistingArgsFunc;
							var thisChar, tempChar;
							var isAnotherParan=false, anotherParanClosed = false;

							pos = currentPos;

							//find args forward
							while({
								thisChar = doc.getChar(pos);
								switch(thisChar)
								{"("}{opCount = opCount + 1}
								{")"}{cpCount = cpCount + 1}
								{"\n"}{isAnotherParan = false}{thisChar = ")"; doc.selectRange(pos+1,0);doc.selectedString = ")"};
								(isAnotherParan || thisChar != ")");
							},{
								existingArgsString = existingArgsString ++ thisChar;
								if(anotherParanClosed){isAnotherParan = false};
								if(cpCount >= opCount){anotherParanClosed = true}{isAnotherParan = true};
								pos = pos + 1
							});


							//set variables
							opCount = 0;
							cpCount = 0;
							end = pos;
							pos = currentPos-1;
							existingArgsString = existingArgsString.reverse;
							anotherParanClosed = false;

							//find Args backward
							while({
								thisChar = doc.getChar(pos);
								switch(thisChar)
								{")"}{cpCount = cpCount + 1}
								{"("}{opCount = opCount + 1};
								(isAnotherParan || thisChar != "(");
							},{
								existingArgsString = existingArgsString ++ thisChar;
								if(anotherParanClosed){isAnotherParan = false};
								if(opCount >= cpCount){anotherParanClosed = true}{isAnotherParan = true};
								pos = pos - 1
							});

							//set variables
							start = pos+1;
							existingArgsString = existingArgsString.reverse;
							pos = pos -1;

							existingArgsAry = Array(12);

							//split arguments
							splitExistingArgsFunc = {
								var codeArray = Array(12);
								var string="", opCount=0, cpCount=0;
								var isAnotherParan=false, anotherParanClosed=false;

								existingArgsString.do{|char|
									switch(char)
									{$(}{opCount = opCount + 1}
									{$)}{cpCount = cpCount + 1};
									if(isAnotherParan || char != $,){
										string = string ++ char;
										if(anotherParanClosed){isAnotherParan = false};
										if(cpCount >= opCount){anotherParanClosed = true}{isAnotherParan = true}
									}{
										codeArray.add(string.replace(" ",""));
										string = "";
									}
								};
								if(string.notEmpty){codeArray.add(string.replace(" ",""))};
								codeArray;
							};

							existingArgsAry = splitExistingArgsFunc.value;

							//////////

							//find method
							while({
								thisChar = doc.getChar(pos);
								thisChar != "."
							},{
								method_ = method_++ thisChar;
								pos = pos - 1;
							});
							method_ = method_.reverse.asSymbol;
							pos = pos-1;

							//find UGen
							while({
								thisChar = doc.getChar(pos);
								tempChar = doc.getChar(pos+1);
								if(thisChar.ascii[0].notNil){
									thisChar[0].isAlphaNum || tempChar[0].isLower;
								}{
									false
								}
							},{
								ugen_ = ugen_ ++ thisChar;
								pos = pos - 1;
							});
							ugen_ = ugen_.reverse.interpret;


							[ugen_, method_, existingArgsAry];
						};



						/////////

						# ugen, method, existingArgs = findCode.value();

						////////

						if(ugen.notNil){
							var arguments, defaults, argString = "";
							var argDict;
							var isUGen=true;

							if(ugen.new.isUGen.not){"Target is not UGen".warn;isUGen=false};
							method = ugen.class.findRespondingMethodFor(method);
							arguments = method.argNames.drop(1);
							defaults = method.prototypeFrame.drop(1);


							if(existingArgs[0].notNil){
								existingArgs.do{|thisArg, i|
									if(thisArg.contains(":").not){
										defaults[i] = thisArg;
									}{
										var which, value, index;
										which = thisArg.copyFromStart(thisArg.find(":")-1).asSymbol;
										value = thisArg.copyToEnd(thisArg.find(":")+1);
										index = arguments.indexOf(which);

										if(index.notNil){
											defaults[index] = value
										}{
											arguments.add(which);
											defaults.add(value);
											"(%) is not a valid argument".format(which).error;
										}
									};
								};
							};

							arguments.do({|argName, i|
								var value;
								value = defaults[i];
								switch(unicode)
								{11}
								{
									if(isUGen){
										if(value.isString.not,{
											if(modifiers.isShift,{
												argString = argString ++ "\\" ++ "%.kr(%), ".format(argName, value ? nil)},{
												argString = argString ++ "%: %, ".format(argName, value ? nil)},{
											})
										},{
											argString = argString ++ "%, ".format(value ? nil);
										});
									}
								}
								{10}
								{argString = argString ++ "%, ".format(value ? nil)}
							});


							doc.selectRange(start,end-start);
							doc.selectedString = argString.drop(-2);


						}{
							"Target not found".error;
						}
				},);

				//auto fill ".set"
				if(unicode == 19 && modifiers.isCtrl,{
					var start, pos = 0, thisChar;
					var string="", tempString="", target = "", targetClass;
					var keysValues;
					start = doc.selectionStart;
					3.do{|i| tempString = tempString ++ doc.getChar(start-1-i)};
					if(tempString == "tes",{
						if(doc.getChar(start-4)!=".",
							{"Syntax error".error},
							{
								pos = start-5;
								while({
									thisChar = doc.getChar(pos);
									((thisChar.ascii[0] == nil) || (thisChar.ascii[0] == 10) || (thisChar.ascii[0] == 32)).not
								},{
									target = target ++ thisChar;
									pos = pos - 1;
								});
								target = target.reverse;
								targetClass = target.interpret.class;
								if(((targetClass == NodeProxy) || (targetClass == Ndef)),{

									keysValues = target.interpret.controlKeysValues;
									if(keysValues.size > 0,{
									(keysValues.size/2).do({|i|
										string = string ++ target ++ ".set('" ++ keysValues[i*2] ++ "', " ++ keysValues[i*2+1].asString ++ ");\n"
									});
									doc.selectRange(start-(target.size+4), target.size+4);
									doc.selectedString = string;
									});
								},{"Target not supported".error});
						});
					});
				});
			};
			Document.globalKeyDownAction = Document.globalKeyDownAction.addFunc(func);
			exists = true;
			"AutoArgs mode enabled".postln;
		}

	}


	*saveSettings {
		var file;
		file = File(Snippet.filenameSymbol.asString.dirname +/+ "autoArgsSettings.scd", "w");
		file.put("//AutoArgs settings file\n\n");
		file.put("AutoArgs.autoStart = %;\n".format(autoStart.asString));
		file.close
	}



	*disable {
		if(exists == true){
			Document.globalKeyDownAction.removeFunc(func);
			exists=false
		}
	}

	*isRunning {
		^exists;
	}


}