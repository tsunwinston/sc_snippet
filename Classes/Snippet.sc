Snippet {

	classvar mainFunc, changeDefaultsFunc;
	classvar <>snippetDict, userSnippetDict;
	classvar <>autoStart, <>autoSave;
	classvar tempDocSize = 0;
	classvar exist = false;
	classvar snipListView;
	classvar <>shortcutOrder;


	*initClass {

		if(Platform.ideName == "scqt",{

		(Snippet.filenameSymbol.asString.dirname +/+ "snippetSettings.scd").load;

		autoStart ?? {autoStart = true; this.saveSettings};

		if(autoStart,{{this.enable}.defer(1)});

		ShutDown.add({
			this.saveSettings;
			if(autoSave,{this.save});
		});
		});

	}

	*enable{
		if(exist.not,{
			var snips;
			var ctrlDownTime = 0;

			snippetDict = IdentityDictionary(128);

			(Snippet.filenameSymbol.asString.dirname +/+ "snippetsDefaults.scd").load;


			mainFunc = {|doc, char, modifiers, unicode, keycode|

				if(modifiers.isCtrl && unicode==65533 && keycode==59,{
					var now;
					now = Main.elapsedTime;

					//double press Control to trigger snippet;
					if((now-ctrlDownTime < 0.5), {

						var allSnippets, codeFragment = "";
						var currentPos, snippetSize, thisSnippet, snippetPos, findCode;
						var start, end;


						//add from document text;
						if(doc.selectionSize > 0,{

							var newSnipWindow, screenBounds;
							var textField, edited=false, label;
							screenBounds = Window.screenBounds;

							newSnipWindow = Window("New snippet", Rect(screenBounds.width/2-125,screenBounds.height/2+30,260,60),false, false).alwaysOnTop_(true).background_(Color(1, 1, 1, 0.65)).front;
							label = StaticText(newSnipWindow, Rect(10, 2, 240, 15)).font_(Font.sansSerif(12).boldVariant).string_("New Snippet: ").stringColor_(Color.black);
							textField = TextField(newSnipWindow, Rect(10, 20, 240, 30)).string_(" Insert key... (press ESC to cancel)").stringColor_(Color.grey);

							textField.mouseDownAction_({|view|
								view.string_("");
								view.stringColor_(Color.black);
							});

							textField.keyDownAction_({|view, char, mod, unicode, keycode|
								if(edited.not,{edited=true; view.string_("")});
								switch(unicode,
									27, {newSnipWindow.close},
									13, {
										var isDuplicated, thisKey;
										thisKey = view.string.asSymbol;
										isDuplicated = snippetDict.keys.includes(thisKey);
										if(isDuplicated,{
											// var askOverwriteWindow,
											doc.selectedString.addSnippet(view.string.asSymbol);
										},{
											doc.selectedString.addSnippet(view.string.asSymbol);
										});
										newSnipWindow.close;
									}
								)
							});
						});



						if(doc.selectionSize == 0,{

							allSnippets = snippetDict.keys;
							currentPos = doc.selectionStart;

							findCode = {
								var pos, thisChar, code="";
								var lim=7, index=0;

								pos = currentPos;
								while({
									thisChar = doc.getChar(pos);
									((thisChar.ascii[0] == nil) || (thisChar.ascii[0] == 10) || (thisChar.ascii[0] == 32) || (index > lim)).not
								},{
									code = code ++ thisChar;
									pos = pos + 1;
									index = index + 1;
								});

								end = pos - 1;
								code = code.reverse;
								pos = currentPos - 1;
								index = 0;


								while({
									thisChar = doc.getChar(pos);
									((thisChar.ascii[0] == nil) || (thisChar.ascii[0] == 10) || (thisChar.ascii[0] == 32) || (index > lim)).not
								},{
									code = code ++ thisChar;
									pos = pos - 1;
									index = index + 1;
								});

								start = pos + 1;

								code = code.reverse;

							};

							codeFragment = findCode.value.toLower;

							allSnippets.do{|key|
								var loc;
								loc = codeFragment.find(key.asString);
								if(loc.notNil){
									if(thisSnippet.isNil){
										thisSnippet = key;
										snippetPos = loc;
									}{
										if(key.asString.size >= thisSnippet.asString.size){
											thisSnippet = key;
											snippetPos = loc;
										}
									}
								}
							};

							if(snippetPos.notNil){
								var thisCode, removedDashesCode, jumpTo=10;

								thisCode = snippetDict[thisSnippet];
								removedDashesCode = thisCode.replace("$$", "");

								currentPos = start + snippetPos;
								snippetSize = thisSnippet.asString.size;
								doc.selectRange(currentPos,snippetSize);
								doc.selectedString = removedDashesCode;

								if(thisCode.contains("$$"),{
									var poss;
									poss = thisCode.findAll("$$").clump(2);
									poss = poss.collect{|j,i|
										var beginPos, endPos, length;
										beginPos = j[0]-(i*4);
										endPos = beginPos + (j[1]-j[0]) - 2;
										length = endPos-beginPos;
										[beginPos + currentPos, length];
									};

									tempDocSize = doc.string.size;
									doc.selectRange(poss[0][0], poss[0][1]);
									changeDefaultsFunc.value(currentPos, removedDashesCode.size + currentPos - 1, poss);
								});
							};
						});
					});
					ctrlDownTime=now;
				});


				//code shortcuts
				if(modifiers.isCmd,{

					var keycodeOrder, which;
					keycodeOrder = [18, 19, 20, 21, 23, 22, 26, 28, 25];
					if(keycodeOrder.includes(keycode),{
						which = shortcutOrder[keycodeOrder.indexOf(keycode)];
						which !? {doc.selectedString = snippetDict[which].replace("$$", "")++"\n"};
					});
				});


				//auto paranthesis;
				if(modifiers.isCtrl && modifiers.isShift && keycode == 25,{
					var pos, string, startPos, endPos;
					pos = doc.selectionStart;
					string = doc.string;
					startPos = (string.copyFromStart(pos).reverse.find("\n\n\n"));
					if(startPos.notNil,{
						startPos = pos - startPos;
						doc.selectRange(startPos, 0);
						if(string[startPos+1] != $(,{doc.selectedString = "("});
					},{
						doc.selectRange(0, 0);
						doc.selectedString = ("(\n");
					});

					endPos = string.copyToEnd(pos).find("\n\n");
					if(endPos.notNil,{
						endPos = endPos + pos;
						doc.selectRange(endPos + 2,0);
						doc.selectedString = ")";
					},{
						doc.selectRange(string.size+1,0);
						doc.selectedString = "\n)";
					})

				});


				//auto variables fill;
				//press ctrl+shift+v after variable declaration;
				if(modifiers.isCtrl && modifiers.isShift && unicode == 22,{
					var str, strSize;
					str = doc.string.copyFromStart(doc.selectionStart).reverse;
					strSize = str.find("{");
					str = str.copyFromStart(strSize).reverse;
					str = str.copyToEnd(str.find("var"));
					["{", "\n", "var", " ", "\t"].do{|find| str = str.replace(find, "")};
					str = str.replace(";",",");
					str = str.replace(","," = \n\t");
					doc.selectedString = str;
					doc.selectRange(doc.selectionStart + str.find(" = ") + 3,0);
				})

			};


			changeDefaultsFunc = {
				arg snipStart, snipEnd, defaultsPos;
				var keyJumpFunc, mouseJumpFunc;
				mouseJumpFunc = {|doc|
					var pos = doc.selectionStart;

					if((pos < snipStart) || (pos > snipEnd),{
						Document.globalKeyDownAction = Document.globalKeyDownAction.removeFunc(keyJumpFunc);
						doc.mouseDownAction = doc.mouseDownAction.removeFunc(mouseJumpFunc);
					})
				};

				keyJumpFunc = {|doc, char, modifiers, unicode, keycode|
					var pos = doc.selectionStart;
					if((pos >= snipStart) && (pos <= snipEnd),{
						if((keycode == 30)||(keycode == 33)&&modifiers.isCtrl,{
							var targetPosIndex, targetPos, docSizeDelta;
							targetPosIndex = defaultsPos.detectIndex{|j| j[0] > pos};

							if(targetPosIndex.isNil,{
								Document.current.mouseDownAction = Document.current.mouseDownAction.removeFunc(mouseJumpFunc);
								Document.globalKeyDownAction = Document.globalKeyDownAction.removeFunc(keyJumpFunc);
							},{

								docSizeDelta = doc.string.size-tempDocSize;
								tempDocSize = doc.string.size;
								defaultsPos.do{|j, i| if(i!=0,{j[0] = j[0] + docSizeDelta})};
								snipEnd = snipEnd + docSizeDelta;
								targetPos = switch(keycode,
									30,{defaultsPos.at(targetPosIndex)},
									33,{defaultsPos.clipAt(targetPosIndex-1)}
								);
								doc.selectRange(targetPos[0],targetPos[1])
							});

						})
					},{
						Document.globalKeyDownAction = Document.globalKeyDownAction.removeFunc(keyJumpFunc);
					})
				};
				Document.current.mouseDownAction = Document.current.mouseDownAction.addFunc(mouseJumpFunc);
				Document.globalKeyDownAction = Document.globalKeyDownAction.addFunc(keyJumpFunc);
			};


			Document.globalKeyDownAction = Document.globalKeyDownAction.addFunc(mainFunc);
			exist = true;
			"Snippet mode enabled".postln;

		};
		);

	}


	*disable {
		if(exist == true){
			Document.globalKeyDownAction.removeFunc(mainFunc);
			exist = false;
		}
	}


	*saveSettings {
		var file;
		file = File(Snippet.filenameSymbol.asString.dirname +/+ "snippetSettings.scd", "w");
		file.put("//Snippet setup file\n\n");
		file.put("Snippet.autoStart = %;\n".format(autoStart.asString));
		file.put("Snippet.autoSave = %;\n".format(autoSave !? autoSave.asString ?? true));
		file.close
	}


	*gui {
		var window, codeView;
		var labelKeys, labelCodes;

		window = Window("snippet list", Rect(Window.screenBounds.width/15, Window.screenBounds.height/2 -150, 300, 335), false, true).front;
		window.background_(Color(0.7, 0.7, 0.7, 1));
		window.alwaysOnTop = true;
		window.layout = VLayout(
			labelKeys = StaticText(),
			snipListView = ListView(),
			labelCodes = StaticText(),
			codeView = TextView()
		);

		window.view.keyDownAction_({|view, char, mod, unicode, keycode|
			if(keycode == 53,{window.close;});
		});

		labelKeys.string_("Snippet Keys: ").font_(Font.sansSerif(12).boldVariant);
		labelCodes.string_("Code: ").font_(Font.sansSerif(12).boldVariant);
		snipListView.items_(snippetDict.keys.asArray.sort.collect{|j| j.asString})
		.selectionMode_(\single)
		.minHeight_(window.bounds.height/3*1.3)
		.maxHeight_(window.bounds.height/3*1.3)
		.action_({|view| codeView.string_(snippetDict[view.items[view.value].asSymbol])})
		.enterKeyAction_({|view|
			var str;
			str = snippetDict[view.items[view.value].asSymbol];
			Document.current.selectedString = (str.replace("$$", "") ++ "\n");
		});

		codeView.enterInterpretsSelection = true;
		codeView.minHeight_(window.bounds.height/3);
		codeView.maxHeight_(window.bounds.height/3);
		codeView.mouseDownAction_({|view| view.string = view.string.replace("$$", "")});

	}

	*updateGui {
		snipListView !? {snipListView.items_(snippetDict.keys.asArray.sort.collect{|j| j.asString})};
	}


	*save {
		if(userSnippetDict.notNil,{
			if(userSnippetDict.notEmpty,{
				var oldFile, newFile;
				var oldStr, newStr, newStrKeyValues="";
				var docIsOpened=false, defaultDocIndex;
				oldFile = File(Snippet.filenameSymbol.asString.dirname +/+ "snippetsDefaults.scd", "r");
				oldStr = oldFile.readAllString;
				oldFile.close;

				defaultDocIndex = Document.allDocuments.collect{|doc| doc.title}.detectIndex{|title| title == "snippetsDefaults.scd"};
				if(defaultDocIndex.notNil,{Document.allDocuments[defaultDocIndex].close});

				newFile = File(Snippet.filenameSymbol.asString.dirname +/+ "snippetsDefaults.scd", "w");
				newStr = "\nSnippet.addSnippets([\n\n%]);";
				userSnippetDict.keys.do{|key|
					newStrKeyValues = newStrKeyValues ++ "'%' -> \n".format(key.asString) ++ userSnippetDict[key].replace("\"", "\\\"").quote ++ ",\n\n"};

				newStr = newStr.format(newStrKeyValues);
				newFile.put(oldStr ++ "\n\n//Added on %".format(Date.gmtime.asString) ++ newStr);
				newFile.close;
				"New snippets are saved sucessfully".postln;
				userSnippetDict = IdentityDictionary(24);
				this.updateGui;

			});
		});
	}



	*removeSnippet{|key, removeFromFile=false|
		snippetDict.removeAt(key);
		if(removeFromFile,{
			var oldFile, rmStart, rmEnd, str;
			var newFile;
			oldFile = File(Snippet.filenameSymbol.asString.dirname +/+ "snippetsDefaults.scd", "r");
			str = oldFile.readAllString;
			rmStart = str.find(key.asString);
			rmStart = str[0..rmStart].findBackwards("\n");
			rmEnd = str.copyToEnd(rmStart+1).find("\n\n");
			rmEnd !? {rmEnd = rmEnd + rmStart + 4} ?? {"Key not found!".error};
			str = str[0..rmStart] ++ str.copyToEnd(rmEnd);
			oldFile.close;
			newFile = File(Snippet.filenameSymbol.asString.dirname +/+ "snippetsDefaults.scd", "w");
			newFile.put(str);
			newFile.close;
			this.updateGui;
		});
	}



	//add a single snippet
	*addSnippet{|keyValPair|
		if(keyValPair.isKindOf(Association),{
			if(keyValPair.key.class == Symbol && keyValPair.value.class == String,{
				if(snippetDict.includesKey(keyValPair.key).not,{
					snippetDict.add(keyValPair);
					userSnippetDict ?? {userSnippetDict = IdentityDictionary(24)};
					if(exist,{userSnippetDict.add(keyValPair)});
					this.updateGui;
				});
			},{"Argument is not valid.".error;});
		},{"Argument is not an Association.".error;})
	}

	//add an Array of Snippets (associations of key(symbol) to value(string of code))
	*addSnippets{|keyValPairs|
		if(keyValPairs.class != Array,{
			"Argument must be an array".error
		},{
			keyValPairs.do{|pair| this.addSnippet(pair)};
		})
	}


	*edit{
		Document.open(Snippet.filenameSymbol.asString.dirname ++ "/snippetsDefaults.scd");
	}


	*settings {
		Document.open(Snippet.filenameSymbol.asString.dirname ++ "/snippetSettings.scd");
	}


	*keys {
		snippetDict.keys.do{|j| j.postln};
		^snippetDict.keys;
	}


	*list {
		var maxSize;
		maxSize = snippetDict.keys.asArray.collect{|key| key.asString.size}.maxItem;
		snippetDict.keys.asArray.sort.do{|key| Post << key.asString.padLeft(maxSize, " ") << " -> " << snippetDict[key].replace("\n", "")[0..50] << "..." << Char.nl;};
		userSnippetDict.keys.asArray.sort.do{|key| Post << key.asString.padLeft(maxSize, " ") << " -> " << snippetDict[key].replace("\n", "")[0..50] << "..." << Char.nl;};
	}



	*search {|keyword|
		var returnKeys;
		returnKeys = Set(28);
		snippetDict.valuesKeysDo{|val, key| if(val.toLower[0..20].find(keyword.toLower).notNil || key.asString.toLower.find(keyword.toLower).notNil,{returnKeys.add(key)})};
		returnKeys.asArray.sort.do{|key|
			var code;
			code = snippetDict[key].replace("\n", "");
			Post << key.asString.padLeft(8) << " -> " << code.[0..50].cs << if(code.size > 50,{"..."}) << Char.nl};
		^returnKeys;
	}


	*isEnabled {
		^exist;
	}

}


/////////////////////////////////////////////////////////////////////////

+ Symbol {
	addSnippet {|code|
		Snippet.addSnippet(Association(this, code.asString));
	}
}


+ String {
	addSnippet {|key|
		Snippet.addSnippet(Association(key.asSymbol,this));
	}
}
