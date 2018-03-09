const execa = require('execa');
const chalk = require('chalk');
const chalkrw = require('chalk-rainbow');

const onMsg = (msg) => {
    console.log(`\n\n${msg}\n\n`);
}

const onError = (msg) => onMsg(
    msg
    .map((line, index) =>
        index === 0
        ? `🙅   `+chalk.red(line)
        : `    `+line
    )
    .join("\n")
)

const onSuccess = (msg) => onMsg(
    msg
    .map((line, index) =>
        index === 0
        ? `😻   `+chalk.green(line)
        : `    `+line
    )
    .join("\n")
)

const npmCmd = (cmd) =>
    execa('npm', ['run',cmd], {
        stdio: 'inherit',
    });


module.exports = {
	npmCmd,
	onSuccess,
	onError,
	onMsg
}
