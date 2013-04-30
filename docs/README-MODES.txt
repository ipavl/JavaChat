MODES
=====

The following is a list of modes that are able to be set on another user or channel by using the following command:

	> /mode +/-<mode> [parameters]

Operator modes (user must have mode +o to set these):

	- Ban (+b)
		- this will ban a user by name
	- Unban (-b)
		- this will temporarily unban a user by name
	- Moderation mode (+m)
		- puts the channel into "moderated" mode; only voiced (+v), operators (+o), and administrators (+A) can speak
		- setting mode -m will remove this
	- Make another user an operator (+o)
		- this will temporarily give another user operator status
	- Remove operator status (-o)
		- this will temporarily remove a user's operator status
	- Give voice to a user (+v)
		- this will temporarily give another user voice (able to talk in a +m channel)
	- Remove voice from a user (-v)
		- this will temporarily remove a user's voice status

Administrator modes (user must have mode +A to set these):

	- all Operator modes
	- Permanently give a user operator status / auto-op (+O)
		- this will give a user operator status everytime they enter the channel
	- Permanently voice a user / auto-voice (+V)
		- this will give a user voice everytime they enter the channel

Console modes (only the console can set these modes):

	- Give Administrator status to a user (+A)
		- this will permanently give a user administrative status and auto-admin them when they join the channel