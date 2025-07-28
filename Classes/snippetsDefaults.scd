//default snippets

Snippet.shortcutOrder = # [px, indev, outdev, syndf, syn, pdf];


Snippet.addSnippets([

	//SynthDefs

	\syndf ->
	"SynthDef('$$foo$$',{|$$out=0,freq=440,pan=0,amp=1,gate=1$$| \n\t$$ $$\n\tOut.ar($$out$$, $$ $$)\n}).add;",

	\syn ->
	"$$x$$ = Synth('$$foo$$');",


	//Patterns

	\pdf ->
	"Pdef('$$foo$$',Pbind(\n\t'instrument','$$default$$',\n\t'scale',Scale.$$major$$,\n\t'degree',$$Pseq([0,1,2,3],inf)$$,\n\t'amp',$$Pseq([0.1],inf)$$,\n\t'dur',$$Pseq([1],inf)$$\n));",

	\pdfmidi ->
	"Pdef('$$foo$$',Pbind(\n\t'type','midi',\n\t'midiout',m,\n\t'chan',0,\n\t'degree',$$Pseq([0,1,2,3],inf)$$,\n\t'amp',$$Pseq([0.1],inf)$$,\n\t'dur',$$Pseq([1],inf)$$\n));",

	\pbd ->
	"Pbind(\n\t'instrument','$$default$$',\n\t'scale',Scale.$$major$$,\n\t'degree',$$Pseq([0,1,2,3],inf)$$,\n\t'amp',$$Pseq([0.1],inf)$$,\n\t'dur',$$Pseq([1],inf)$$\n)",

	\pdfp ->
	"Pdef('$$foo$$').play;",

	\pdfq ->
	"Pdef('$$foo$$').quant_($$4$$);",

	\pdfs ->
	"Pdef('$$foo$$').stop;",

	\pbdf ->
	"Pbindef('$$foo$$','$$ $$', $$ $$);",

	\pseq ->
	"Pseq([$$0, 1, 2, 3$$],inf)",

	\pwhite ->
	"Pwhite($$0, 1$$,inf)",

	\prand ->
	"Prand([$$0,1$$],inf)",




	//Ndef
	\ndf ->
	"Ndef('$$foo$$',{$$ $$});",

	\ndfp ->
	"Ndef('$$foo$$').play;",

	\ndfq ->
	"Ndef('$$foo$$').quant_($$4$$);",

	\ndfs ->
	"Ndef('$$foo$$').stop;",

	\ndf0->
	"Ndef('$$foo$$')[0] = {$$ $$};",

	\ndff ->
	"Ndef('$$foo$$')[$$1$$] = '$$filter$$' -> {|in| $$ $$};",

	\ndfst ->
	"Ndef(\'$$foo$$').set($$\freq$$, $$ $$);",

	\ndfmp ->
	"Ndef(\'$$foo$$').map($$\freq$$, $$ $$);",

	\ndfm ->
	"NdefMixer(Server.default);",

	//ProxySpace
	\px ->
	"p = ProxySpace($$s.boot$$).push;",

	\pxm ->
	"ProxyMixer($$p$$);",

	//control structures
	\if ->
	"if($$ $$, {$$ $$});",

	\ife ->
	"if($$ $$, {$$ $$},{$$ $$});",

	\while ->
	"while({$$ $$}, {$$ $$});",

	\for ->
	"for($$ $$, $$ $$, {\n$$ $$\n})",

	\case ->
	"case\n{$$ $$}{$$ $$}\n{$$ $$}{$$ $$};",

	\switch ->
	"switch($$ $$,\n$$ $$,{$$ $$},\n$$ $$,{$$ $$})",

	\do ->
	"do{|$$i$$| $$ $$}",


	//other
	\bfr ->
	"Buffer.read($$s$$, $$Platform.resourceDir +/+ \"sounds/a11wlk01.wav\"$$);",

	\bfa ->
	"Buffer.alloc($$s$$, s.sampleRate * $$2$$, 1);",

	\nadd ->
	"NetAddr(\"$$192.168.1.1$$\", 57120)",

	\dist ->
	"($$in$$ * \\preGain.kr(4)).distort * ($$ -12$$.dbamp)",

	\egen ->
	"EnvGen.kr($$Env.adsr$$, gate, doneAction:$$2$$);\n",

	\out ->
	"Out.ar($$out$$, $$ $$);",

	\oscdf ->
	"OSCdef('$$foo$$',{|msg| $$ $$},'$$/path$$');",

	\tdf ->
	"Tdef('$$foo$$',{$$ $$});",

	\tdfp ->
	"Tdef('$$foo$$').play;",

	\tempo ->
	"(\n$$t$$ = TempoClock.default;\n$$t$$.tempo = $$1$$\n);",

	'//' ->
	"//------------------------------------------------------------------------------------//\n",

]);


//Added on Thu May 18 18:25:48 2017
Snippet.addSnippets([

	'indev' ->
	"$$s$$.options.inDevice = \"Built-in Microph\";",

]);

//Added on Thu May 18 18:26:00 2017
Snippet.addSnippets([

	'outdev' ->
	"$$s$$.options.outDevice = \"Built-in Output\";",

]);

//Added on Mon May 22 00:31:13 2017
Snippet.addSnippets([

	'8ch' ->
	"[ -22.5, 22.5, -67.5, 67.5, -112.5, 112.5, -157.5, 157.5 ]",

]);

//Added on Mon May 22 04:39:57 2017
Snippet.addSnippets([

	'3sig' ->
	"var sig1, sig2, sig3, mix;
sig1 = $$ $$
sig2 =
sig3 =
mix = ",

	'2sig' ->
	"var sig1, sig2, mix;
sig1 = $$ $$
sig2 =
mix = ",

	'1sig' ->
	"var sig;
sig = $$ $$",

]);



//Added on Tue May 23 09:04:43 2017
Snippet.addSnippets([

'sr' ->
"s.options.sampleRate = $$44100$$;",

]);


//Added on Fri Nov 17 10:45:48 2017
Snippet.addSnippets([

'verb' ->
"var out = in; 12.do{out = AllpassC.ar(out, 0.05, 0.05.rand, 6)};out",

]);
