# SuperCollider Snippet

**SuperCollider Snippet** is a quick and efficient code generation tool designed for the SuperCollider IDE. It allows users to expand shorthand text into pre-written SuperCollider code snippets by simply typing the shorthand and double-tapping the Control key. This tool streamlines the coding process, saving time and reducing repetitive typing.

---

## Features

- **Quick Code Generation**: Transform shorthands into fully-written SuperCollider code snippets with just a double-tap of the Control key.
- **Customizable Snippets**: Easily view and edit the library of available code snippets.
- **Seamless Integration**: Works directly within the SuperCollider IDE.

---

## Installation

1. Copy the entire `sc_snippet` folder into the SuperCollider **Extensions directory**:
   ```supercollider
   Platform.userExtensionDir
   ```

2. Launch SuperCollider, recompile, and use the following commands to manage snippets:
Snippet.list – View all available snippets.
Snippet.edit – Access and edit the snippet library.

---

## Usage

1. Enable the snippet tool by running:
	```supercollider
	Snippet.enable;
	```

2. Type a shorthand snippet in the IDE and double-tap the Control key. The shorthand will automatically transform into the corresponding code snippet.

---

## Examples
1. Example 1: Expanding ndf
Input:
	```supercollider
	ndf
	```

Action: Double-tap the Control key.

* Output:
	```supercollider
	Ndef('foo', { });
	```

2. Example 2: Expanding pdf
Input:

	```supercollider
	pdf
	```
Action: Double-tap the Control key.

* Output:

```supercollider
	Pdef('foo', Pbind(
	    'instrument', 'default',
	    'degree', Pseq([0, 1, 2, 3], inf),
	    'dur', Pseq([1], inf)
	));
	```

---

## Managing Snippets

View Snippets: Run Snippet.list to display all available shorthand snippets and their corresponding code.
Edit Snippets: Run Snippet.edit to open and modify the snippet library according to your needs.



